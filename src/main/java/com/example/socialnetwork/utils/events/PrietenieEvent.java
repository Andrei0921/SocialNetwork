package com.example.socialnetwork.utils.events;

import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Utilizator;

public class PrietenieEvent implements Event {
    private ChangeTypeEvent type;
    private Prietenie data, oldData;

    public PrietenieEvent(ChangeTypeEvent type, Prietenie data) {
        this.type = type;
        this.data = data;
    }

    public PrietenieEvent(ChangeTypeEvent type, Prietenie data, Prietenie oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeTypeEvent getType() {
        return type;
    }

    public Prietenie getData() {
        return data;
    }

    public Prietenie getOldData() {
        return oldData;
    }
}
