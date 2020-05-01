package server;

import server.auth_service.AuthService;
import server.auth_service.SqliteAuthService;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    private final int PORT = 8189;

    private List<ClientHandler> clients;
    private AuthService authService;

    public MyServer() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new SqliteAuthService();
            authService.start();
            clients = new ArrayList<>();

            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился, ожидание авторизации");
                new ClientHandler(this, socket);
            }
        } catch (Exception e) {
            System.out.println("Ошибка в работе сервера: " + e.getMessage());
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public synchronized void broadcastMsgExcept(String msg, ClientHandler exceptClientHandler) {
        for (ClientHandler o : clients) {
            if (o != exceptClientHandler) {
                o.sendMsg(msg);
            }
        }
    }

    public synchronized void personalMsg(String fromNick, String toNick, String msg) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(fromNick) || o.getName().equals(toNick)) {
                o.sendMsg(String.format("%s -> %s: %s", fromNick, toNick, msg));
            }
        }
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
    }

    public synchronized String getConnectedNicks() {
        StringBuilder result = new StringBuilder();
        for (ClientHandler cl : clients) {
            result.append(cl.getName()).append(" ");
        }

        return result.toString().trim();
    }
}
