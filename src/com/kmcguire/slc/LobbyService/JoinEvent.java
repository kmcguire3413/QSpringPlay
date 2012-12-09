package com.kmcguire.slc.LobbyService;

public class JoinEvent extends Event {
    private String          channel;

    public String getChannel() {
        return channel;
    }

    public JoinEvent(String channel) {
        this.channel = channel;
    }
}
