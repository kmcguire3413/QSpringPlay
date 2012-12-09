package com.kmcguire.slc.LobbyService;

public class JoinedEvent extends Event {
    private String              channel;
    private String              user;

    public String getChannel() {
        return channel;
    }

    public String getUser() {
        return user;
    }

    public JoinedEvent(String channel, String user) {
        this.channel = channel;
        this.user = user;
    }
}
