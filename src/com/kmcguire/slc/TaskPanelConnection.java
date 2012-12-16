/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.LobbyService;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QWidget;

/**
 * This provides a task panel which will display connection information, and
 * allow the connection to be closed.
 * @author kmcguire
 */
public class TaskPanelConnection extends QTaskPanel {
    private ProgramServices             services;
    private QWidget                     surface;
    private QTimer                      timer;
    
    public TaskPanelConnection(ProgramServices _services) {
        services = _services;
        surface = new QWidget(this);
        
        timer = new QTimer();
        timer.timeout.connect(this, "tick()");
        timer.setSingleShot(false);
        timer.setInterval(2000);
        timer.start();
        
        bytesIn = new QLabel(surface);
        bytesIn.show();
        /*
        bytesOut = new QLabel(surface);
        bytesOut.show();
        eventsIn = new QLabel(surface);
        eventsIn.show();
        eventsUnhandled = new QLabel(surface);
        eventsUnhandled.show();
        linesIn = new QLabel(surface);
        linesIn.show();
        lineBufSize = new QLabel(surface);
        lineBufSize.show();
        */
        
        resize(10, 40);
        setStyleSheet("background-color: green;");
    }
    
    private QLabel              bytesIn;
    private QLabel              bytesOut;
    private QLabel              eventsIn;
    private QLabel              eventsUnhandled;
    private QLabel              linesIn;
    private QLabel              lineBufSize;
    
    /**
     * This is called by the QTimer so we can poll and 
     * update our information on the widget;
     */
    public void tick() {
        LobbyService        ls;
        
        // bytesIn, bytesOut, eventsIn, eventsUnhandled, linesIn, lineBufSize
        ls = services.getLobbyService();
        
        bytesIn.move(0, 0);
        bytesIn.setText(
                String.format("BytesIn: %d BytesOut:%d EventsIn:%d EventsUnhandled:%d LinesIn:%s LineBufSize:%d",
                ls.getBytesIn(), ls.getBytesOut(), ls.getEventsIn(), ls.getEventsUnhandled(), ls.getLinesIn(), ls.getLineBufSize()
        ));
    }
    
    @Override
    public void resizeEvent(QResizeEvent event) {
        surface.resize(this.width(), this.height());
        bytesIn.resize(surface.width(), surface.height());
    }
}
