package com.kmcguire.slc.LobbyService;

public class JoinBattleEvent extends Event {
    private int             id;
    private long            hash;

    public JoinBattleEvent(int id, long hash) {
        this.id = id;
        this.hash = hash;
    }

    public int getId() {
        return id;
    }

    public long getHash() {
        return hash;
    }
    
}
