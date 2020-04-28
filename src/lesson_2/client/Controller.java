package lesson_2.client;

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
    TextArea textArea;

    @FXML
    TextField msgField, loginField;

    @FXML
    HBox msgPanel, authPanel;

    @FXML
    PasswordField passField;

    @FXML
    ListView<String> clientsList;

    private lesson_2.client.Network network;

    private boolean authenticated;
    private String nickname;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
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

    public void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
            alert.showAndWait();
        });
    }

    public void linkCallbacks() {
        network = new lesson_2.client.Network();
        network.setCallOnException(args -> showAlert(args[0].toString()));

        network.setCallOnCloseConnection(args -> setAuthenticated(false));

        network.setCallOnAuthenticated(args -> {
            setAuthenticated(true);
            nickname = args[0].toString();
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
                    }
                } else {
                    textArea.appendText(msg + "\n");
                }
            });
        });
    }
}