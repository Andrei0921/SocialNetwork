package com.example.socialnetwork.domain;

import java.time.LocalDateTime;

public class Prietenie extends Entity<Long>{
    private LocalDateTime friendsFrom;
    private Long idUser1;
    private Long idUser2;
    private String status;

    public Prietenie(Long idUser1, Long idUser2,String status) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.status = status;
        this.friendsFrom = LocalDateTime.now();
    }

    public Prietenie(Long idUser1, Long idUser2, String status, LocalDateTime friendsFrom) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.status = status;
        this.friendsFrom = friendsFrom;
    }


    public Long getIdUser1() {
        return idUser1;
    }
    public Long getIdUser2() {
        return idUser2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }
    public void setDate(LocalDateTime d) {
        friendsFrom = d;
    }



}
