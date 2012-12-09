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
    private String          title;
    private String          mod;
    
    public BattleOpenedEvent(
            int _id, int _type, int _nat, String _user, String _host,
            int _port, int _maxPlayers, boolean _hasPass,
            int _rank, long _hash, String _map, String _title,
            String _mod
    ) {
        host = _host;
        id = _id;
        type = _type;
        nat = _nat;
        user = _user;
        port = _port;
        maxPlayers = _maxPlayers;
        hasPass = _hasPass;
        rank = _rank;
        hash = _hash;
        map = _map;
        title = _title;
        mod = _mod;
    }

}
