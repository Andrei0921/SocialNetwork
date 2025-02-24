package com.example.socialnetwork.utils.events;

import com.example.socialnetwork.domain.Utilizator;

public class UtilizatorEvent implements Event {
    private ChangeTypeEvent type;
    private Utilizator data, oldData;

    public UtilizatorEvent(ChangeTypeEvent type, Utilizator data) {
        this.type = type;
        this.data = data;
    }

    public UtilizatorEvent(ChangeTypeEvent type, Utilizator data, Utilizator oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeTypeEvent getType() {
        return type;
    }

    public Utilizator getData() {
        return data;
    }

    public Utilizator getOldData() {
        return oldData;
    }
}

