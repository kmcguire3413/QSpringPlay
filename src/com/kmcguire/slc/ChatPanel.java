package com.kmcguire.slc;

public class ChatPanel extends Panel {
    private String                  channel;
    private MainWindow              mwin;
    
    public ChatPanel(MainWindow mwin, String channel) {
        this.mwin = mwin;
        this.channel = channel;
    }
    
    @Override
    public String getTitle() {
        return String.format("#%s", channel);
    }
}
