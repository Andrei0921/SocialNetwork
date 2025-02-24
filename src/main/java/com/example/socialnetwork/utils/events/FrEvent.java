package com.example.socialnetwork.utils.events;

import com.example.socialnetwork.domain.FriendRequest;
import com.example.socialnetwork.domain.Utilizator;

public class FrEvent  implements Event{
    private ChangeTypeEvent type;
    private FriendRequest data, oldData;

    public FrEvent(ChangeTypeEvent type, FriendRequest data) {
        this.type = type;
        this.data = data;
    }

    public FrEvent(ChangeTypeEvent type, FriendRequest data, FriendRequest oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeTypeEvent getType() {
        return type;
    }

    public FriendRequest getData() {
        return data;
    }

    public FriendRequest getOldData() {
        return oldData;
    }

}
