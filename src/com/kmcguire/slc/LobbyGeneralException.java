package com.kmcguire.slc;

/**
 * I need a general exception class for the lobby client. So this is it. It is
 * nothing perfect, but it should work where needed until it can be improved or
 * something better used.
 * @author kmcguire
 */
public class LobbyGeneralException extends Exception {
    public LobbyGeneralException(String msg) {
        super(msg);
    }
}
