package com.kmcguire.slc.LobbyService;

public class SaidEvent extends Event {
    private String                  channel;
    private String                  user;

    public SaidEvent(String channel, String user, String message) {
        this.channel = channel;
        this.user = user;
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }
    
    private String                  message;
}
