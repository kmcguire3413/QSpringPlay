package com.kmcguire.slc.LobbyService;

public class BattleOpenedEvent extends Event {
    private int             id;
    private int             type;
    private int             nat;
    private String          user;
    private String          host;
    private int             port;
    private int             maxPlayers;
    private boolean         hasPass;
    private int             rank;
    private long            hash;
    private String          map;
    private String          title;
    private String          mod;

    public BattleOpenedEvent(int id, int type, int nat, String user, String host, int port, int maxPlayers, boolean hasPass, int rank, long hash, String map, String title, String mod) {
        this.id = id;
        this.type = type;
        this.nat = nat;
        this.user = user;
        this.host = host;
        this.port = port;
        this.maxPlayers = maxPlayers;
        this.hasPass = hasPass;
        this.rank = rank;
        this.hash = hash;
        this.map = map;
        this.title = title;
        this.mod = mod;
    }

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

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getMod() {
        return mod;
    }

    public int getNat() {
        return nat;
    }

    public int getPort() {
        return port;
    }

    public int getRank() {
        return rank;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public String getUser() {
        return user;
    }

    public String getHost() {
        return host;
    }

}
