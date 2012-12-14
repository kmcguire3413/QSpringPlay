package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.Event;
import java.util.Set;

public class BattleUsersChangedEvent extends Event {
    private int             battleId;
    private Set<String>     players;

    public BattleUsersChangedEvent(int battleId, Set<String> players) {
        this.battleId = battleId;
        this.players = players;
    }

    public int getBattleId() {
        return battleId;
    }

    public Set<String> getPlayers() {
        return players;
    }
}
