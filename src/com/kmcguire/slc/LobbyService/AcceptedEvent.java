package com.kmcguire.slc.LobbyService;

public class AcceptedEvent extends Event {
    private String          user;

    public String getUser() {
        return user;
    }
    
    public AcceptedEvent(String user) {
        this.user = user;
    }
}
