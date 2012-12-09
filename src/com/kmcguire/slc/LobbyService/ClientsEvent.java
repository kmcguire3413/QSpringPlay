package com.kmcguire.slc.LobbyService;

public class ClientsEvent extends Event {
    private String              channel;
    private String[]            clients;

    public ClientsEvent(String channel, String[] clients) {
        this.channel = channel;
        this.clients = clients;
    }

    public String getChannel() {
        return channel;
    }

    public String[] getClients() {
        return clients;
    }
}
