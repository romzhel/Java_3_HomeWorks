package server.auth_service;

public interface AuthService {
    void start();

    String getNickByLoginPass(String login, String pass);

    void stop();
}
