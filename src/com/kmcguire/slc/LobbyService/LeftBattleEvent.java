package com.kmcguire.slc.LobbyService;

public class LeftBattleEvent extends Event {
    private int             id;

    public int getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public LeftBattleEvent(int id, String user) {
        this.id = id;
        this.user = user;
    }
    private String          user;
}
