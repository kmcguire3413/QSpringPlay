package com.kmcguire.slc.LobbyService;

public class BattleStatus {
    public int getAlly() {
        return ally;
    }

    public void setAlly(int ally) {
        this.ally = ally;
    }

    public void setHandicap(int handicap) {
        this.handicap = handicap;
    }

    public void setPlayer(boolean player) {
        this.player = player;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getHandicap() {
        return handicap;
    }

    public boolean isPlayer() {
        return player;
    }

    public boolean isReady() {
        return ready;
    }

    public int getSide() {
        return side;
    }

    public int getSync() {
        return sync;
    }

    public int getTeam() {
        return team;
    }
    
    private boolean                  ready;
    private int                      team;
    private int                      ally;
    private boolean                  player;
    private int                      handicap;
    private int                      sync;
    private int                      side;
    private int                      status;

    public int getStatus() {
        writeStatus();
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public BattleStatus(int status) {
        this.status = status;
        readStatus();
    }
    
    public void writeStatus() {
        status = 
                ((ready ? 1 : 0) << 1) |
                (team << 2) |
                (ally << 6) |
                ((player ? 1 : 0) << 10) |
                (handicap << 11) |
                (sync << 22) |
                (side << 24);
        // nothing
    }
    
    private void readStatus() {
        // nothing
        ready = ((status >> 1) & 1) == 1;
        team = (status >> 2) & 0xf;
        ally = (status >> 6) & 0xf;
        player = ((status >> 10) & 1) == 1;
        handicap = (status >> 11) & 0x7f;
        sync = (status >> 22) & 3;
        side = (status >> 24) & 0xf;
    }    
}
