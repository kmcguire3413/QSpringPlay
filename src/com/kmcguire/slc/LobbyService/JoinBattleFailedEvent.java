package com.kmcguire.slc.LobbyService;

public class JoinBattleFailedEvent extends Event {

    public JoinBattleFailedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    private String              message;
}
