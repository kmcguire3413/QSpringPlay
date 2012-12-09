package com.kmcguire.slc.LobbyService;

public class DeniedEvent extends Event {
    private String                  message;

    public DeniedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
