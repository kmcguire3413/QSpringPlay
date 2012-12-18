package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.AddUserEvent;
import com.kmcguire.slc.LobbyService.BattleClosedEvent;
import com.kmcguire.slc.LobbyService.BattleOpenedEvent;
import com.kmcguire.slc.LobbyService.ClientStatusEvent;
import com.kmcguire.slc.LobbyService.EventHandler;
import com.kmcguire.slc.LobbyService.JoinEvent;
import com.kmcguire.slc.LobbyService.JoinFailedEvent;
import com.kmcguire.slc.LobbyService.JoinedBattleEvent;
import com.kmcguire.slc.LobbyService.LeftBattleEvent;
import com.kmcguire.slc.LobbyService.LobbyService;
import com.kmcguire.slc.LobbyService.LoginInfoEndEvent;
import com.kmcguire.slc.LobbyService.RemoveUserEvent;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QSplitter;
import com.trolltech.qt.gui.QTabWidget;
import com.trolltech.qt.gui.QWidget;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainWindow extends QWidget implements ProgramServices {
    private QTimer                  netTimer;
    private LobbyService            lobbyService;
    private QTabWidget              tabWidget;
    private QSplitter               vsplitter;
    private QTaskArea               taskArea;
    
    private Map<String, LobbyUser>          users;
    private Map<Integer, Set<String>>       battleList;
    // this is used to track chat panel widgets so during a disconnect
    // and a reconnect we do not recreate them if they already exist
    // which they will because the user might want to read the history
    private HashMap<String, ChatPanel>      chatPanels;
    
    private Map<String, QIcon>              flagIcons;
    
    public MainWindow() {
        users = new HashMap<String, LobbyUser>();
        battleList = new HashMap<Integer, Set<String>>();
        
        chatPanels = new HashMap<String, ChatPanel>();
        
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
        
        flagIcons = new HashMap<String, QIcon>();
        
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
        addPanel(MultiplayerPanelZkStyle.createInstance(this));
        try {
            addPanel(new BattleRoomPanel(this));
        } catch (LobbyGeneralException ex) {
            System.out.printf("warning: could not create BattleRoomPanel.. do you have multiple MainWindow objects?");
            System.out.printf("warning: or maybe you have already created a BattleRoomPanel? only one can exist....");
        }
    }
    
    @Override
    public QTaskArea getTaskArea() {
        return taskArea;
    }
    
    public ProgramServices getProgramServices() {
        return this;
    }
    
    public QIcon getFlagIcon(String code) {
        QPixmap                 pixmap;
        QIcon                   icon;
        
        pixmap = new QPixmap();
        
        if (!flagIcons.containsKey(code)) {
            try {
                pixmap.loadFromData(SpringLobbyClient.loadResource(String.format("flags/%s.png", code)));
                icon = new QIcon(pixmap);
                flagIcons.put(code, icon);
            } catch (IOException ex) {
                System.out.printf("I/O exception trying to read flags/%s.png\n", code);
                icon = null;
            }
        } else {
            icon = flagIcons.get(code);
        }
        
        return icon;
    }
    
    public LobbyUser getLobbyUser(String user) {
        return users.get(user);
    }
    
    @EventHandler
    private void onRemoveUser(RemoveUserEvent event) {
        // remove the user from any battle they were in because
        // i do not know if the server will send a message saying 
        // that they left the battle
        if (users.get(event.getUser()).getBattleId() > -1) {
            if (battleList.get(users.get(event.getUser()).getBattleId()) != null) {
                battleList.get(users.get(event.getUser()).getBattleId()).remove(event.getUser());
            }
        }
        
        users.remove(event.getUser());
    }
    
    @EventHandler
    private void onClientStatus(ClientStatusEvent event) {
        LobbyUser       lu;
        
        lu = users.get(event.getUser());
        
        if (lu != null) {
            lu.setStatus(event.getStatus());
        }
    }
    
    /**
     * The battle list is maintained by the MainWindow class and this
     * list provides a Set of the user names that are currently in this
     * battle. This is a helpful service because otherwise this would be
     * re-implemented any where it is needed. This method may need to be
     * moved into a the LobbyService namespace? --kmcguire
     * @param id            the battle identifier as a integer
     * @return              a Set<String> containing the users in this battle or null
     */
    public Set<String> getBattleList(int id) {
        return battleList.get(id);
    }
    
    @EventHandler
    private void onJoinedBattle(JoinedBattleEvent event) {
        LobbyUser                   lu;
        BattleUsersChangedEvent     buce;
        
        lu = users.get(event.getUser());
        
        if (lu != null) {
            lu.setBattleId(event.getId());
        }
        battleList.get(event.getId()).add(event.getUser());
        buce = new BattleUsersChangedEvent(event.getId(), battleList.get(event.getId()));
        lobbyService.callEvent(buce);        
    }
    
    @EventHandler
    private void onLeftBattle(LeftBattleEvent event) {
        BattleUsersChangedEvent     buce;
        
        battleList.get(event.getId()).remove(event.getUser());
        
        buce = new BattleUsersChangedEvent(event.getId(), battleList.get(event.getId()));
        lobbyService.callEvent(buce);
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
        battleList.put(event.getId(), new HashSet<String>());
        
        //if (event.getTitle().indexOf("Newbies") > -1) {
            //lobbyService.joinBattle(event.getId());
        //}
    }
    
    @EventHandler
    private void onBattleClosed(BattleClosedEvent event) {
        // just to make sure let us set all users known to be
        // in this battle to -1 just to be on the safe side
        for (String user : battleList.get(event.getId())) {
            users.get(user).setBattleId(-1);
        }
        
        battleList.remove(event.getId());
    }
    
    /**
     * This happens we you join a channel and we check if we have already
     * created a chat channel for it and if it exists then we just
     * drop out of the method.
     * @param event 
     */
    @EventHandler
    private void onJoin(JoinEvent event) {
        ChatPanel           cp;
        
        cp = chatPanels.get(event.getChannel());
        
        /*
         * If we already have a chat panel then it is already registered
         * with the lobby service since we do not reinstance the lobby
         * service. So lets just silenty drop this and not create another
         * chat tab for this channel.
         */
        if (cp != null) {
            return;
        }
        
        cp = new ChatPanel(this, event.getChannel());
        tabWidget.addTab(cp, cp.getTitle());
        chatPanels.put(event.getChannel(), cp);
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
