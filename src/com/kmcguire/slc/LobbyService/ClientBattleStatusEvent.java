package com.kmcguire.slc.LobbyService;

public class ClientBattleStatusEvent extends Event {
    private String                  user;
    private int                     status;
    private int                     color;

    public ClientBattleStatusEvent(String user, int status, int color) {
        this.user = user;
        this.status = status;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public int getStatus() {
        return status;
    }

    public String getUser() {
        return user;
    }
}
