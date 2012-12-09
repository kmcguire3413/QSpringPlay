package com.kmcguire.slc.LobbyService;

public class BattleClosedEvent extends Event {
    private int                 id;

    public int getId() {
        return id;
    }

    public BattleClosedEvent(int id) {
        this.id = id;
    }
}
