package com.kmcguire.slc.LobbyService;

public class ClientStatusEvent extends Event {
    private String          user;
    private int             status;

    public int getStatus() {
        return status;
    }

    public String getUser() {
        return user;
    }

    public ClientStatusEvent(String user, int status) {
        this.user = user;
        this.status = status;
    }
}
