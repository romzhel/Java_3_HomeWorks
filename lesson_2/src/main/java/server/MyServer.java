package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.auth_service.AuthService;
import server.auth_service.SqliteAuthService;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MyServer {
    private static final Logger LOGGER = LogManager.getLogger(MyServer.class);
    private final int PORT = 8189;
    private final int IDLE_THREADS_COUNT = 0;
    private final int MAX_THREADS_COUNT = 3;
    private List<ClientHandler> clients;
    private AuthService authService;

    public MyServer() {
        LOGGER.info("Запуск сервера");
        ExecutorService executorService = new ThreadPoolExecutor(IDLE_THREADS_COUNT, MAX_THREADS_COUNT,
                0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                LOGGER.warn("Достигнут лимит подключений клиентов ({})", MAX_THREADS_COUNT);
                //(ClientHandler)r - обработчик подключения клиента, которому не хватило потока, можно отправить ему
                //данные для подключения к другому серверу и т.п., например:
                new Thread(() -> {
                    ((ClientHandler) r).sendMsg("/reconnect to");
                    ((ClientHandler) r).closeConnection();
                }).start();
            }
        });

        try (ServerSocket server = new ServerSocket(PORT)) {
            LOGGER.trace("Создан сокет сервера - {}", server);
            authService = new SqliteAuthService();
            authService.start();
            clients = new ArrayList<>();

            LOGGER.info("Сервер запущен и готов к подключению клиентов");
            while (true) {
                Socket socket = server.accept();
                LOGGER.info("Подключился клиент - {}", socket);
                ClientHandler clientHandler = null;
                try {
                    clientHandler = new ClientHandler(this, socket);
                    executorService.submit(clientHandler);
                } catch (Exception e) {
                    LOGGER.error("Ошибка создания обработчика клиента - {}", socket);
                    socket.close();
                }
            }
        } catch (Exception e) {
            LOGGER.fatal("Ошибка в работе сервера - {}", e.getMessage(), e);
        } finally {
            if (authService != null) {
                authService.stop();
            }
            executorService.shutdownNow();
            LOGGER.info("Работа сервера завершена");
        }
    }

    public synchronized AuthService getAuthService() {
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
