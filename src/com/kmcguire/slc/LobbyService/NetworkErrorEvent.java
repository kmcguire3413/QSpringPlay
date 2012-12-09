package com.kmcguire.slc.LobbyService;

public class NetworkErrorEvent extends Event {
    private String          message;
    
    NetworkErrorEvent(String _message) {
        message = _message;
    }
    
    public String getMessage() {
        return message;
    }
}