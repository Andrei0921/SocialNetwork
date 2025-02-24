package com.example.socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;

public class ReplyMessage extends Message {
    private Message replied_m;
    public ReplyMessage(Utilizator from, List<Utilizator> to, String message, LocalDateTime data,Message replied_m) {
        super(from, to, message, data);
        this.replied_m = replied_m;
    }
    public Message getReplied_m() {
        return replied_m;
    }
    public void setReplied_m(Message replied_m) {
        this.replied_m = replied_m;
    }

    @Override
    public String toString() {
        return "ReplyMessage{" +
                "repliedMessage=" + replied_m +
                '}';
    }

}
