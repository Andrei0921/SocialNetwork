package com.example.socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Message extends Entity<Long>{
    private Utilizator from;
    private String message;
    private List<Utilizator> to;
    private LocalDateTime data;
    private Long repliedMessageId;

    public Message(  Utilizator from, List<Utilizator> to,String message, LocalDateTime data) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
    }

    public Long getRepliedMesageId() {
        return repliedMessageId;
    }

    public void setRepliedMessageId(Long repliedMesageId) {
        this.repliedMessageId = repliedMesageId;
    }

    public Utilizator getFrom() {
        return from;
    }
    public void setFrom(Utilizator from) {
        this.from = from;

    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public List<Utilizator> getTo() {
        return to;
    }
    public void setTo(List<Utilizator> to) {
        this.to = to;
    }
    public LocalDateTime getData() {
        return data;
    }
    public void setData(LocalDateTime data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "(" + getFrom().toString() + ")\n" + message + "\n(" + data.format(DateTimeFormatter.ofPattern("hh:mm dd/MM/yy")) + ")";
    }


}