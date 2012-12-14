package com.kmcguire.slc.LobbyService;

public class JoinedBattleEvent extends Event {
    private int             id;
    private String          user;
    private String          userScriptPassword;

    public int getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getUserScriptPassword() {
        return userScriptPassword;
    }

    public JoinedBattleEvent(int id, String nick, String userScriptPassword) {
        this.id = id;
        this.user = nick;
        this.userScriptPassword = userScriptPassword;
    }
}
