/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.LobbyService;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPushButton;
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
    private LoginPanel                  loginPanel;
    
    public TaskPanelConnection(ProgramServices services, LoginPanel loginPanel) {
        this.loginPanel = loginPanel;
        this.services = services;
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
        button = new QPushButton(surface);
        button.show();
        
        button.clicked.connect(this, "btnLogout(boolean)");
        resize(10, 40);
        //setStyleSheet("background-color: green;");
        
        this.setFrameShadow(Shadow.Raised);
        this.setFrameShape(Shape.WinPanel);
        
        tick();
    }
    
    private QPushButton         button;
    private QLabel              bytesIn;
    private QLabel              bytesOut;
    private QLabel              eventsIn;
    private QLabel              eventsUnhandled;
    private QLabel              linesIn;
    private QLabel              lineBufSize;
    
    public void btnLogout(boolean checked) {
        loginPanel.btnLoginClicked(false);
    }
    
    /**
     * This is called by the QTimer so we can poll and 
     * update our information on the widget;
     */
    public void tick() {
        LobbyService        ls;
        
        // bytesIn, bytesOut, eventsIn, eventsUnhandled, linesIn, lineBufSize
        ls = services.getLobbyService();
        
        bytesIn.setText(
                String.format("BytesIn: %d BytesOut:%d EventsIn:%d EventsUnhandled:%d LinesIn:%s LineBufSize:%d",
                ls.getBytesIn(), ls.getBytesOut(), ls.getEventsIn(), ls.getEventsUnhandled(), ls.getLinesIn(), ls.getLineBufSize()
        ));
        button.setText("Logout");
    }
    
    @Override
    public void resizeEvent(QResizeEvent event) {
        surface.resize(this.width(), this.height());
        bytesIn.move(10, 0);
        bytesIn.resize(surface.width() - 75, surface.height());
        button.move(surface.width() - 75, 5);
        button.resize(70, 30);
    }
}
