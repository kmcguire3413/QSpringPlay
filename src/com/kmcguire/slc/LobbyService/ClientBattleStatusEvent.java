package com.kmcguire.slc.LobbyService;

public class ClientBattleStatusEvent extends Event {
    private String                  user;
    private BattleStatus            status;
    private int                     color;

    public int getColor() {
        return color;
    }

    public BattleStatus getStatus() {
        return status;
    }

    public String getUser() {
        return user;
    }

    public ClientBattleStatusEvent(String user, int status, int color) {
        this.user = user;
        this.status = new BattleStatus(status);
        this.color = color;
    }
}
