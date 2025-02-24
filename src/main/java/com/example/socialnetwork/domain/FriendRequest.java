package com.example.socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendRequest extends Entity<Long> {
    private Long fromUserId;
    private Long toUserId;
    private FriendshipStatus status;
    private LocalDateTime time;

    public FriendRequest(Long fromUserId, Long toUserId, FriendshipStatus status, LocalDateTime time) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.status = status;
        this.time = time;
    }
    public Long getFromUserId() {
        return fromUserId;
    }
    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }
    public Long getToUserId() {
        return toUserId;
    }
    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
    public FriendshipStatus getStatus() {
        return status;
    }
    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }
    public LocalDateTime getTime() {
        return time;
    }
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(fromUserId, that.fromUserId) && Objects.equals(toUserId, that.toUserId) && Objects.equals(status, that.status) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash( fromUserId, toUserId, time);
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", requestType='" + status + '\'' +
                ", time=" + time +
                '}';
    }


}
