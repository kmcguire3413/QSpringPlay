package com.kmcguire.slc.LobbyService;

public class RemoveUserEvent extends Event {
    private String          user;

    public String getUser() {
        return user;
    }

    public RemoveUserEvent(String user) {
        this.user = user;
    }
}
