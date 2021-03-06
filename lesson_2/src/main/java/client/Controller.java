package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    MenuBar mnuBar;

    @FXML
    TextArea textArea;

    @FXML
    TextField msgField, loginField;

    @FXML
    HBox msgPanel, authPanel;

    @FXML
    PasswordField passField;

    @FXML
    ListView<String> clientsList;

    private Network network;

    private boolean authenticated;
    private String nickname;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        mnuBar.setVisible(authenticated);
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        msgPanel.setVisible(authenticated);
        msgPanel.setManaged(authenticated);
        clientsList.setVisible(authenticated);
        clientsList.setManaged(authenticated);
        if (!authenticated) {
            nickname = "";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthenticated(false);
        clientsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String nickname = clientsList.getSelectionModel().getSelectedItem();
                msgField.setText("/w " + nickname + " ");
                msgField.requestFocus();
                msgField.selectEnd();
            }
        });
        linkCallbacks();
        network.connect();
    }

    public void sendAuth() {
        network.sendAuth(loginField.getText(), passField.getText());
        loginField.clear();
        passField.clear();
    }

    public void sendMsg() {
        if (network.sendMsg(msgField.getText())) {
            msgField.clear();
            msgField.requestFocus();
        }
    }

    public void changeNick() {
        TextInputDialog nickInputDialog = new TextInputDialog();
        nickInputDialog.setHeaderText("Смена ника " + nickname);
        nickInputDialog.setContentText("Введите новый ник");

        String newNick = nickInputDialog.showAndWait().orElse("");

        if (!newNick.trim().isEmpty() && !newNick.equals(nickname)) {
            network.sendMsg("/cn " + newNick);
        } else {
            showAlert("Ник не был изменён");
        }
    }

    public void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
            alert.showAndWait();
        });
    }

    public void linkCallbacks() {
        network = new Network();
        network.setCallOnException(args -> showAlert(args[0].toString()));

        network.setCallOnCloseConnection(args -> {
            setAuthenticated(false);
            ChatHistoryLogger.getInstance().close();
        });

        network.setCallOnAuthenticated(args -> {
            setAuthenticated(true);
            nickname = args[0].toString();

            try {
                ChatHistoryLogger.getInstance().init(nickname);
            } catch (Exception e) {
                showAlert("Не удалось инициализировать модуль записи истории событий в файл.\n" + e.getMessage());
            }
            textArea.clear();
            ChatHistoryLogger.getInstance().getHistory(100).forEach(s -> textArea.appendText(s + "\n"));
        });

        network.setCallOnMsgReceived(args -> {
            Platform.runLater(() -> {
                String msg = args[0].toString();
                if (msg.startsWith("/")) {
                    if (msg.startsWith("/a")) {
                        clientsList.getItems().add(msg.replaceFirst("/a ", ""));
                    } else if (msg.startsWith("/r")) {
                        clientsList.getItems().remove(msg.replaceFirst("/r ", ""));
                    } else if (msg.startsWith("/cs")) {
                        clientsList.getItems().clear();
                        clientsList.getItems().addAll(Arrays.asList(msg.replaceFirst("/cs ", "")
                                .split("\\s")));
                    } else if (msg.startsWith("/cne")) {
                        showAlert("Не удалось сменить ник\nПричина: " + msg.replaceFirst("/cne ", ""));
                    } else if (msg.startsWith("/cns")) {
                        nickname = msg.replaceFirst("/cns ", "");
                    }
                } else {
                    textArea.appendText(msg + "\n");
                    ChatHistoryLogger.getInstance().saveMessage(msg + "\n");
                }
            });
        });
    }
}