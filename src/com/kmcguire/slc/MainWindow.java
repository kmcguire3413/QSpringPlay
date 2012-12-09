package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.AuthenticationEvent;
import com.kmcguire.slc.LobbyService.BattleOpenedEvent;
import com.kmcguire.slc.LobbyService.EventHandler;
import com.kmcguire.slc.LobbyService.JoinEvent;
import com.kmcguire.slc.LobbyService.JoinFailedEvent;
import com.kmcguire.slc.LobbyService.LobbyService;
import com.kmcguire.slc.LobbyService.LoginInfoEndEvent;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QScrollArea;
import com.trolltech.qt.gui.QSplitter;
import com.trolltech.qt.gui.QTabWidget;
import com.trolltech.qt.gui.QWidget;

public class MainWindow extends QWidget {
    private QTimer              netTimer;
    private LobbyService        lobbyService;
    private QTabWidget          tabWidget;
    private QSplitter           vsplitter;
    private QTaskArea           taskArea;
    
    public MainWindow() {
        netTimer = new QTimer();
        netTimer.setSingleShot(false);
        netTimer.setInterval(200);
        netTimer.timeout.connect(this, "netTimerEvent()");
        netTimer.start();
        
        lobbyService = new LobbyService();
        lobbyService.registerForEvents(this);
        
        vsplitter = new QSplitter(Qt.Orientation.Vertical, this);
        vsplitter.resize(this.width(), this.height());
        
        tabWidget = new QTabWidget();
        tabWidget.show();
        tabWidget.resize(this.width(), this.height());
        
        taskArea = new QTaskArea();
        
        vsplitter.addWidget(tabWidget);
        vsplitter.addWidget(taskArea);
        vsplitter.show();       
        
        addPanel(new LoginPanel());
    }
    
    public LobbyService getLobbyService() {
        return lobbyService;
    }
    
    public void addPanel(Panel panel) {
        tabWidget.addTab(panel, panel.getTitle());
    }
    
    @Override
    public void resizeEvent(QResizeEvent event) {
        vsplitter.resize(this.width(), this.height());
    }
    
    @EventHandler
    public void onBattleOpened(BattleOpenedEvent event) {
        if (event.getTitle().indexOf("Newbies") > -1) {
            //lobbyService.joinBattle(event.getId());
        }
    }
    
    @EventHandler
    public void onJoin(JoinEvent event) {
        addPanel(new ChatPanel(this, event.getChannel()));
    }
    
    @EventHandler
    public void onLobbyAuthentication(AuthenticationEvent event) {
        event.setUser("kmcguire");
        event.setPass("tty5413413");
        event.setClientVersion("SLC0.0.0");
    }
    
    @EventHandler
    public void onJoinFailed(JoinFailedEvent event) {
        System.out.printf("joinfailed %s %s\n", event.getChannel(), event.getReason());
    }
    
    @EventHandler
    public void onLoginInfoEnd(LoginInfoEndEvent event) {
        lobbyService.joinChannel("mychannel");
    }
        
    private void netTimerEvent() {
        lobbyService.tick();
    }
}
