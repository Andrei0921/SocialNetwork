package com.example.socialnetwork.utils.events;

import com.example.socialnetwork.domain.Message;

public class MessageEvent implements Event {
    private ChangeTypeEvent type;
    private Message data, oldData;

    public MessageEvent(Message message) {
        this.data = message;
    }


    public ChangeTypeEvent getType() {
        return type;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }
}

