package com.kmcguire.slc.LobbyService;

public class SaidBattleExEvent extends Event {
    private String              user;
    private String              message;

    public String getMessage() {
        return message;
    }

    public SaidBattleExEvent(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public String getUser() {
        return user;
    }
}
