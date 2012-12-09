package com.kmcguire.slc.LobbyService;

public class SaidBattleEvent extends Event {
    private String              user;
    private String              message;

    public SaidBattleEvent(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }
}
