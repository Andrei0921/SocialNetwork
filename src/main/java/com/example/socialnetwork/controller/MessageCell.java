package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.ReplyMessage;
import com.example.socialnetwork.domain.Utilizator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class MessageCell extends ListCell<Message> {
    private HBox container = new HBox();
    private VBox messageBox = new VBox();
    private Label replyLabel = new Label();
    private Label messageLabel = new Label();

    private Utilizator currentUser;

    public MessageCell(Utilizator currentUser) {
        this.currentUser = currentUser;

        replyLabel.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 1px; -fx-border-radius: 1px;");
        messageLabel.setStyle("-fx-padding: 1px; -fx-border-radius: 1px;");

        messageBox.getChildren().addAll(replyLabel, messageLabel);
        container.getChildren().add(messageBox);
        setGraphic(container);
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty || message == null) {
            setGraphic(null);
        } else {
            if (message instanceof ReplyMessage reply) {
                replyLabel.setText("Răspuns la: " + reply.getReplied_m().getMessage());
                replyLabel.setVisible(true);
            } else {
                replyLabel.setVisible(false);
            }

            messageLabel.setText( message.getMessage());


            boolean isSentByCurrentUser = message.getFrom().equals(currentUser);

            container.getChildren().clear();
            messageBox.getChildren().clear();
            messageBox.getChildren().addAll(replyLabel, messageLabel);

            if (isSentByCurrentUser) {

                container.setAlignment(Pos.CENTER_RIGHT);
                messageBox.setStyle("-fx-background-color: #A8E6CF; -fx-padding: 1px; -fx-border-radius: 1px;");
                container.getChildren().add(messageBox);
            } else {

                container.setAlignment(Pos.CENTER_LEFT);
                messageBox.setStyle("-fx-background-color: #FFDD94; -fx-padding: 1px; -fx-border-radius: 1px;");
                container.getChildren().add(messageBox);
            }


            messageBox.setPadding(new Insets(1));
            container.setSpacing(1);
            setGraphic(container);
        }
    }
}
