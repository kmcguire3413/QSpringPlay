package com.kmcguire.slc.LobbyService;

public class UpdateBattleInfoEvent extends Event {
    private int             id;
    private int             spec;
    private boolean         hasPass;
    private long            hash;
    private String          map;

    public boolean isHasPass() {
        return hasPass;
    }

    public long getHash() {
        return hash;
    }

    public int getId() {
        return id;
    }

    public String getMap() {
        return map;
    }

    public int getSpec() {
        return spec;
    }

    public UpdateBattleInfoEvent(int id, int spec, boolean hasPass, long hash, String map) {
        this.id = id;
        this.spec = spec;
        this.hasPass = hasPass;
        this.hash = hash;
        this.map = map;
    }
}
