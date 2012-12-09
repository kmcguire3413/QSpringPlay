package com.kmcguire.slc.LobbyService;

public class LeftEvent extends Event {
    private String              channel;
    private String              user;

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }

    public LeftEvent(String channel, String user, String message) {
        this.channel = channel;
        this.user = user;
        this.message = message;
    }
    private String              message;
}
