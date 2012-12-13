package com.kmcguire.slc;

public class LobbyUser {
    private String              user;
    private String              country;
    private int                 status;
    private int                 battleId;

    public int getBattleId() {
        return battleId;
    }

    public void setBattleId(int battleId) {
        this.battleId = battleId;
    }

    public boolean isInGame() {
        return (status & 1) > 0;
    }
    
    public boolean isAway() {
        return (status & 2) > 0;
    }
    
    public int getRank() {
        return (status >> 2) & 7;
    }
    
    public boolean isMod() {
        return (status & 32) > 0;
    }
    
    public boolean isBot() {
        return (status & 64) > 0;
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public int getCpu() {
        return cpu;
    }

    public String getUser() {
        return user;
    }
    private int                 cpu;

    public LobbyUser(String user, String country, int cpu) {
        this.user = user;
        this.country = country;
        this.cpu = cpu;
        this.battleId = -1;
    }
}
