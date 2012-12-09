package com.kmcguire.slc.LobbyService;

public class AuthenticationEvent extends Event {
    private String          infoMessage;
    private String          user;
    private String          pass;
    private String          clientVersion;

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }
    
    public AuthenticationEvent(String _infoMessage) {
        infoMessage = _infoMessage;
    }
    
    public String getInfoMessage() {
        return infoMessage;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
