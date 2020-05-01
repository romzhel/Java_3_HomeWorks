package server.auth_service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SqliteAuthService implements AuthService {
    private Connection connection;
    private PreparedStatement getNick;
    private PreparedStatement checkNick;
    private PreparedStatement changeNick;

    @Override
    public void start() throws Exception {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:users.db");
            getNick = connection.prepareStatement("SELECT nick FROM users WHERE login = ? AND password = ?");
            checkNick = connection.prepareStatement("SELECT * FROM users WHERE nick = ?");
            changeNick = connection.prepareStatement("UPDATE users SET nick = ? WHERE nick = ?");
        } catch (Exception e) {
            throw new RuntimeException("не удалось запустить сервер авторизации, ошибка: " + e.getMessage());
        }
    }

    @Override
    public String getNickByLoginPass(String login, String password) throws Exception {
        getNick.setString(1, login);
        getNick.setString(2, password);

        try (ResultSet rs = getNick.executeQuery()) {
            if (rs.next()) {
                return rs.getString("nick");
            } else {
                return null;
            }
        }
    }

    @Override
    public void changeNick(String oldNick, String newNick) throws Exception {
        checkNick.setString(1, newNick);

        try (ResultSet rs = checkNick.executeQuery()) {
            if (rs.next()) {
                throw new RuntimeException("Ник " + newNick + " уже существует");
            }
        }

        changeNick.setString(1, newNick);
        changeNick.setString(2, oldNick);

        if (changeNick.executeUpdate() < 1) {
            throw new RuntimeException("Не удалось сменить ник " + oldNick + " на " + newNick);
        }
    }

    @Override
    public void stop() {
        try {
            changeNick.close();
        } catch (Exception e) {
        }
        try {
            getNick.close();
        } catch (Exception e) {
        }
        try {
            connection.close();
        } catch (Exception e) {
        }
    }
}
