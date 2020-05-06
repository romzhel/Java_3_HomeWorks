package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHandler implements Runnable {
    private final MyServer myServer;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private String name;

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
        } catch (Exception e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }

    @Override
    public void run() {
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    closeConnection();
                }
            }, 30_000);
            authentication();
            timer.cancel();
            readMessages();
        } catch (Exception e) {
            System.out.println("Завершение потока обработки входящих сообщений, причина: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public String getName() {
        return name;
    }

    public void authentication() throws Exception {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/auth")) {
                String[] parts = str.split("\\s");
                String nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                if (nick != null) {
                    if (!myServer.isNickBusy(nick)) {
                        sendMsg("/authok " + nick);
                        sendConnectedContacts();
                        name = nick;
                        myServer.broadcastMsg("/a " + name);
                        myServer.subscribe(this);
                        return;
                    } else {
                        sendMsg("Учетная запись уже используется");
                    }
                } else {
                    sendMsg("Неверные логин/пароль");
                }
            }
        }
    }

    public void readMessages() throws IOException {
        while (true) {
            String strFromClient = in.readUTF();
            if (strFromClient.equals("/end")) {
                return;
            } else if (strFromClient.startsWith("/w")) {
                strFromClient = strFromClient.replaceFirst("/w ", "");
                int spacePos = strFromClient.indexOf(" ");
                myServer.personalMsg(name, strFromClient.substring(0, spacePos), strFromClient.substring(spacePos + 1));
            } else if (strFromClient.startsWith("/cn")) {
                String newNick = strFromClient.replaceFirst("/cn ", "");
                changeNick(newNick);
            } else {
                myServer.broadcastMsg(name + ": " + strFromClient);
            }
        }
    }

    private void changeNick(String newNick) {
        try {
            myServer.getAuthService().changeNick(name, newNick);
            sendMsg("/cns " + newNick);
            myServer.broadcastMsgExcept("/r " + name, this);
            myServer.broadcastMsgExcept("/a " + newNick, this);
            myServer.broadcastMsg(name + " сменил свой ник на " + newNick);
            name = newNick;
        } catch (Exception e) {
            sendMsg("/cne " + e.getMessage());
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendConnectedContacts() {
        String contacts = myServer.getConnectedNicks();
        if (!contacts.isEmpty()) {
            sendMsg("/cs " + contacts);
        }
    }

    public void closeConnection() {
        if (!name.isEmpty()) {
            myServer.unsubscribe(this);
            myServer.broadcastMsg("/r " + name);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        closeConnection();
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
