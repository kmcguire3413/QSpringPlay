package com.kmcguire.slc.LobbyService;

import java.util.ArrayList;

public class NetMessage {
    private String              message;
    
    public NetMessage(String message) {
        this.message = message;
    }
    
    public String getRemaining() {
        String      param;
        
        param = message;
        message = null;
        
        return param;
    }
    
    public int getIntParam() {
        String          param;
        int             x;
                
        param = getWordParam();
        
        try {
            x = Integer.parseInt(param);
        } catch (NumberFormatException ex) {
            return 0;
        }
        
        return x;
    }
    
    public long getLongParam() {
        return Long.parseLong(getWordParam());
    }
    
    public String getWordParam() {
        return getWordParam(' ');
    }

    public String[] getSentenceListParam() {
        ArrayList<String>       l;
        String                  word;
        String[]                out;
        
        l = new ArrayList<String>();
        
        while ((word = getSentenceParam()) != null) {
            l.add(word);
        }
        
        out = new String[l.size()];
        
        l.toArray(out);
        
        return out;
    }
    
    public String[] getWordListParam() {
        ArrayList<String>       l;
        String                  word;
        String[]                out;
        
        l = new ArrayList<String>();
        
        while ((word = getWordParam()) != null) {
            l.add(word);
        }
        
        out = new String[l.size()];
        
        l.toArray(out);
        
        return out;
    }
    
    private String getWordParam(char sep) {
        int         i;
        String      param;
        
        if (message == null) {
            return null;
        }
        
        i = message.indexOf(' ');
        
        if (i > -1) {
            param = message.substring(0, i);
            message = message.substring(i + 1);
            return param;
        }
        
        param = message;
        message = null;
        
        return param;
    }
    
    public String getSentenceParam() {
        return getWordParam('\t');
    }
    
    public boolean getBoolParam() {
        return Boolean.parseBoolean(getWordParam());
    }
}
