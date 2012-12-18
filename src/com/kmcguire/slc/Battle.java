package com.kmcguire.slc;

import java.util.Set;

public class Battle {
    private int          id;
    private int          maxPlayers;
    private boolean      hasPass;
    private String       map;            // changable
    private String       title;
    private String       mod;
    private Set<String>  players;
    private int          cntSpecs;          // changable

    public int getCntSpecs() {
        return cntSpecs;
    }

    public void setCntSpecs(int cntSpecs) {
        this.cntSpecs = cntSpecs;
    }

    public boolean isHasPass() {
        return hasPass;
    }

    public void setHasPass(boolean hasPass) {
        this.hasPass = hasPass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public Set<String> getPlayers() {
        return players;
    }

    public void setPlayers(Set<String> players) {
        this.players = players;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
