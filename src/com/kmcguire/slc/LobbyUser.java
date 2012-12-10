package com.kmcguire.slc;

public class LobbyUser {
    private String              user;
    private String              country;

    public String getCountry() {
        return country;
    }

    public int getCpu() {
        return cpu;
    }

    public String getUser() {
        return user;
    }
    private int                 cpu;

    public LobbyUser(String user, String country, int cpu) {
        this.user = user;
        this.country = country;
        this.cpu = cpu;
    }
}
