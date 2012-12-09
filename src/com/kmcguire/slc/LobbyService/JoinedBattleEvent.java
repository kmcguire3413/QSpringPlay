package com.kmcguire.slc.LobbyService;

public class JoinedBattleEvent extends Event {
    private int             id;
    private String          nick;
    private String          userScriptPassword;

    public int getId() {
        return id;
    }

    public String getNick() {
        return nick;
    }

    public String getUserScriptPassword() {
        return userScriptPassword;
    }

    public JoinedBattleEvent(int id, String nick, String userScriptPassword) {
        this.id = id;
        this.nick = nick;
        this.userScriptPassword = userScriptPassword;
    }
}
