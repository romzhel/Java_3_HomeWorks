package server.auth_service;

public interface AuthService {
    void start() throws Exception;

    String getNickByLoginPass(String login, String pass) throws Exception;

    void changeNick(String oldNick, String newNick) throws Exception;

    void stop();
}
