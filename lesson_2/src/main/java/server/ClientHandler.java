package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHandler implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);
    private final MyServer myServer;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private String name;

    public ClientHandler(MyServer myServer, Socket socket) throws Exception {
        this.myServer = myServer;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.name = "";
    }

    @Override
    public void run() {
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LOGGER.info("Клиент {} отключен, так как не авторизовался", socket);
                    closeConnection();
                }
            }, 30_000);
            LOGGER.info("Запущен таймер ожидания авторизации клиента {} - {} секунд", socket, 30);
            authentication();
            timer.cancel();
            readMessages();
        } catch (Exception e) {
            LOGGER.warn("Завершение работы обработчика клиента {}, причина: {}", socket, e.getMessage());
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
                        LOGGER.info("Клиент {} успешно авторизовался под ником {}", socket, nick);
                        return;
                    } else {
                        LOGGER.warn("Клиент {} попытался авторизоваться под занятым ником {}", socket, nick);
                        sendMsg("Учетная запись уже используется");
                    }
                } else {
                    LOGGER.warn("Клиент {} попытался авторизоваться с неверными учетными данными {}/{}", socket, parts[1], parts[2]);
                    sendMsg("Неверные логин/пароль");
                }
            }
        }
    }

    public void readMessages() throws IOException {
        LOGGER.trace("Начат обмен сообщениями с клиентом {}", socket);
        while (true) {
            String strFromClient = in.readUTF();
            if (strFromClient.equals("/end")) {
                LOGGER.info("Получена команда от клиента {} на завершение работы", socket);
                return;
            } else if (strFromClient.startsWith("/w")) {
                LOGGER.info("Получено персональное сообщение от клиента {} - {}", socket, name);
                strFromClient = strFromClient.replaceFirst("/w ", "");
                int spacePos = strFromClient.indexOf(" ");
                myServer.personalMsg(name, strFromClient.substring(0, spacePos), strFromClient.substring(spacePos + 1));
            } else if (strFromClient.startsWith("/cn")) {
                LOGGER.info("Получена команда от клиента {} на изменение ника", socket);
                String newNick = strFromClient.replaceFirst("/cn ", "");
                changeNick(newNick);
            } else {
                LOGGER.info("Получено сообщение от клиента {} - {}", socket, name);
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
            LOGGER.info("Клиент {} сменил свой ник с {} на {}", socket, name, newNick);
            name = newNick;
        } catch (Exception e) {
            sendMsg("/cne " + e.getMessage());
            LOGGER.warn("Ошибка смены ника клиента {} с {} на {}: {}", socket, name, newNick, e.getMessage());
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
        LOGGER.trace("Закрытие обработчика клиента {}", socket);
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
}
