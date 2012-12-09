package com.kmcguire.slc.LobbyService;

public class ChannelTopicEvent extends Event {
    private String          channel;
    private String          user;
    private int             pos;
    private String          topic;

    public String getChannel() {
        return channel;
    }

    public int getPos() {
        return pos;
    }

    public String getTopic() {
        return topic;
    }

    public String getUser() {
        return user;
    }

    public ChannelTopicEvent(String channel, String user, int pos, String topic) {
        this.channel = channel;
        this.user = user;
        this.pos = pos;
        this.topic = topic;
    }
}
