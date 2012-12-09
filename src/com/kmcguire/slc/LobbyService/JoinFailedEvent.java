package com.kmcguire.slc.LobbyService;

public class JoinFailedEvent extends Event {
    private String          channel;
    private String          reason;

    public String getChannel() {
        return channel;
    }

    public String getReason() {
        return reason;
    }

    public JoinFailedEvent(String channel, String reason) {
        this.channel = channel;
        this.reason = reason;
    }
    
}
