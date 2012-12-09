package com.kmcguire.slc.LobbyService;

public class AddUserEvent extends Event {
    private String          user;
    private String          country;
    private int             cpu;

    public String getCountry() {
        return country;
    }

    public int getCpu() {
        return cpu;
    }

    public String getUser() {
        return user;
    }
    
    public AddUserEvent(String user, String country, int cpu) {
        this.user = user;
        this.country = country;
        this.cpu = cpu;
    }
}
