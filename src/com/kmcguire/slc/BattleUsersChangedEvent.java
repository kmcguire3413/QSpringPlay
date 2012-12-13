package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.Event;

public class BattleUsersChangedEvent extends Event {
    private int             battleId;

    public int getBattleId() {
        return battleId;
    }

    public BattleUsersChangedEvent(int battleId) {
        this.battleId = battleId;
    }
}
