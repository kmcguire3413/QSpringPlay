package com.kmcguire.slc.LobbyService;

public class MotdEvent extends Event {
    private String          message;

    public String getMessage() {
        return message;
    }
    
    public MotdEvent(String message) {
        this.message = message;
    }
}
