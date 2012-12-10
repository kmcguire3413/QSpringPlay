package com.kmcguire.slc;

import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QWidget;

public class ChatPanel extends Panel {
    public String                  channel;
    public MainWindow              mwin;
    public QWidget                 surface;
    
    public ChatPanel(MainWindow mwin, String channel) {
        this.mwin = mwin;
        this.channel = channel;
        
        surface = new QWidget(this);
        surface.setStyleSheet("background-color: #ffff33;");
        surface.show();
    }
    
    @Override
    public String getTitle() {
        return String.format("#%s", channel);
    }
    
    
    @Override
    public void resizeEvent(QResizeEvent event) {
        surface.resize(width(), height());
    }
}
