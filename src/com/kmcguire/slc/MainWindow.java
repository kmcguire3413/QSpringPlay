package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.AddUserEvent;
import com.kmcguire.slc.LobbyService.BattleOpenedEvent;
import com.kmcguire.slc.LobbyService.EventHandler;
import com.kmcguire.slc.LobbyService.JoinEvent;
import com.kmcguire.slc.LobbyService.JoinFailedEvent;
import com.kmcguire.slc.LobbyService.LobbyService;
import com.kmcguire.slc.LobbyService.LoginInfoEndEvent;
import com.kmcguire.slc.LobbyService.RemoveUserEvent;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QSplitter;
import com.trolltech.qt.gui.QTabWidget;
import com.trolltech.qt.gui.QWidget;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends QWidget {
    private QTimer                  netTimer;
    private LobbyService            lobbyService;
    private QTabWidget              tabWidget;
    private QSplitter               vsplitter;
    private QTaskArea               taskArea;
    private Map<String, LobbyUser>  users;
    
    public MainWindow() {
        users = new HashMap<String, LobbyUser>();
        
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
        
        /*
        QWidget     a, b, c;
        
        a = new QWidget();
        b = new QWidget();
        c = new QWidget();
        
        a.setStyleSheet("background-color: #99ff99;");
        b.setStyleSheet("background-color: #9999ff;");
        c.setStyleSheet("background-color: #99ffff;");
        
        a.resize(50, 25);
        b.resize(50, 25);
        c.resize(50, 25);
        
        taskArea.addWidget(a);
        taskArea.addWidget(b);
        taskArea.addWidget(c);
        */
        
        vsplitter.addWidget(tabWidget);
        vsplitter.addWidget(taskArea);
        vsplitter.show();       
        
        addPanel(new LoginPanel(this));
        addPanel(new MultiplayerPanel(this));
    }
    
    public LobbyUser getLobbyUser(String user) {
        return users.get(user);
    }
    
    @EventHandler
    private void onRemoveUser(RemoveUserEvent event) {
        users.remove(event.getUser());
    }
    
    @EventHandler
    private void onAddUser(AddUserEvent event) {
        LobbyUser       lu;
        
        lu = new LobbyUser(event.getUser(), event.getCountry(), event.getCpu());
        
        users.put(event.getUser(), lu);
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
    private void onBattleOpened(BattleOpenedEvent event) {
        if (event.getTitle().indexOf("Newbies") > -1) {
            //lobbyService.joinBattle(event.getId());
        }
    }
    
    @EventHandler
    private void onJoin(JoinEvent event) {
        ChatPanel           cp;
        QWidget             w;

        //w = new QWidget();
        //w.setStyleSheet("background-color: #ffff00;");

        cp = new ChatPanel(this, event.getChannel());
        //cp.setStyleSheet("background-color: #ffff00;");

        tabWidget.addTab(cp, cp.getTitle());
        //tabWidget.addTab(cp, "ChatPanel");
    }
    
    @EventHandler
    private void onJoinFailed(JoinFailedEvent event) {
        System.out.printf("joinfailed %s %s\n", event.getChannel(), event.getReason());
    }
    
    @EventHandler
    private void onLoginInfoEnd(LoginInfoEndEvent event) {
        lobbyService.joinChannel("mychannel");
        //lobbyService.joinChannel("zkdev");
    }
        
    private void netTimerEvent() {
        if (lobbyService != null) {
            lobbyService.tick();
        }
    }
}
