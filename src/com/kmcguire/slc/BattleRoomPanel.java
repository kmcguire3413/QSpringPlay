package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.BattleStatus;
import com.kmcguire.slc.LobbyService.ChannelTopicEvent;
import com.kmcguire.slc.LobbyService.ClientBattleStatusEvent;
import com.kmcguire.slc.LobbyService.ClientsEvent;
import com.kmcguire.slc.LobbyService.EventHandler;
import com.kmcguire.slc.LobbyService.JoinBattleEvent;
import com.kmcguire.slc.LobbyService.JoinedBattleEvent;
import com.kmcguire.slc.LobbyService.JoinedEvent;
import com.kmcguire.slc.LobbyService.LeftBattleEvent;
import com.kmcguire.slc.LobbyService.LeftEvent;
import com.kmcguire.slc.LobbyService.LobbyService;
import com.kmcguire.slc.LobbyService.RequestBattleStatusEvent;
import com.kmcguire.slc.LobbyService.SaidBattleEvent;
import com.kmcguire.slc.LobbyService.SaidEvent;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QListWidget;
import com.trolltech.qt.gui.QListWidgetItem;
import com.trolltech.qt.gui.QPlainTextEdit;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QSplitter;
import com.trolltech.qt.gui.QWidget;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the main panel/widget for the current battle room. There can only
 * exist one of these technically. However, if the lobby server supported a
 * user being in multiple battles then this could be changed to support that.
 * @author kmcguire
 */
public class BattleRoomPanel extends Panel {
    private ProgramServices                services;
    private static BattleRoomPanel         instance;
    
    private int                            cbid;
    private QListWidget                    users;
    private QPlainTextEdit                 chat;
    private QSplitter                      hsplitter;
    private QSplitter                      vsplitter;
    private QLineEdit                      editBox;
    private QWidget                        chatSurface;
    private LobbyService                   ls;

    private TaskPanelBattle                 taskPanel;
    
    private Map<String, QListWidgetItem>   userToListWidget;

    private String                          chatMsg;
    private String                          chatJoinMsg;
    private String                          chatPartMsg;
    private String                          chatJoinBattleMsg;
    private String                          chatNormalMsg;
    
    public BattleRoomPanel(ProgramServices _services) throws LobbyGeneralException {
        if (instance != null) {
            throw new LobbyGeneralException("Only one instance can be created!");
        }
        
        instance = this;
        this.services = _services;
        
        hsplitter = new QSplitter(Qt.Orientation.Horizontal, this);
        vsplitter = new QSplitter(Qt.Orientation.Vertical, this);
        
        editBox = new QLineEdit();
        chat = new QPlainTextEdit();
        users = new QListWidget();
        
        vsplitter.addWidget(chat);
        vsplitter.addWidget(editBox);
        
        //hsplitter.splitterMoved.connect(this, "onSplitterMove(int, int)");
        
        hsplitter.addWidget(vsplitter);
        hsplitter.addWidget(users);
        
        vsplitter.show();
        hsplitter.show();
        editBox.show();
        chat.show();
        users.show();
        
        editBox.returnPressed.connect(this, "onEditBoxEnterPressed()");
        
        List<Integer>       sizes;
        
        sizes = hsplitter.sizes();
        
        sizes.set(0, width() - 200);
        sizes.set(1, 200);
        
        hsplitter.setSizes(sizes);
        
        _resizeEvent(width(), height());
        
        services.getLobbyService().registerForEvents(this);
        
        ls = services.getLobbyService();
        
        userToListWidget = new HashMap<String, QListWidgetItem>();
        
        taskPanel = new TaskPanelBattle(services);
        
        // chatMsg, chatJoinMsg, chatPartMsg, chatJoinBattleMsg, chatNormalMsg
        
        chatMsg = "[$user$]: $msg$";
        chatJoinMsg = "-!- $user$ joined";
        chatPartMsg = "-!- $user$ left";
        chatJoinBattleMsg = "-!- You joined the battle $title$.";
        chat.setStyleSheet("background-color: black; color: gray; font-family: monospace; font-size: 10pt;");
    }
    
    public static BattleRoomPanel getInstance() {
        return instance;
    }
    public static void joinBattle(int bid) {
        BattleRoomPanel     brp;
        
        brp = getInstance();
        
        brp.services.getTaskArea().addWidget(brp.taskPanel);
        brp.taskPanel.configureForBattle(bid);
        
        brp.chat.appendPlainText(String.format("-!- joined battle %d", bid));
        brp.cbid = bid;
        brp.populateUsers();
        brp.services.getLobbyService().joinBattle(bid);
    }
    
    private void populateUsers() {
        Set<String>         allUsers;
        
        allUsers = services.getBattleList(cbid);
        
        userToListWidget = new HashMap<String, QListWidgetItem>();
        users.clear();
        
        if (allUsers == null) {
            System.out.printf("debug: warning: allUsers was empty!");
            return;
        }
        
        for (String user : allUsers) {
            addUserToUsers(user);
        }
    }
    
    @EventHandler
    private void onRequestBattleStatus(RequestBattleStatusEvent event) {
        BattleStatus            bs;
        
        bs = new BattleStatus(0);
        bs.setReady(false);
        bs.setPlayer(false);
        bs.setSync(1);
        
        System.out.printf("debug: sending battle status %d\n", bs.getStatus());
        ls.sendBattleStatus(bs.getStatus(), 0);
    }
    
    private void addUserToUsers(String user) {
        QListWidgetItem         i;
        
        i = new QListWidgetItem();
        userToListWidget.put(user, i);

        i.setText(user);

        users.addItem(i);        
    }
    
    @EventHandler
    private void onJoinedBattle(JoinedBattleEvent event) {
        if (event.getId() == cbid) {
            chat.appendPlainText(String.format("-!- %s has joined the battle", event.getUser()));
            

        }
    }
    
    @EventHandler
    private void onJoinBattle(JoinBattleEvent event) {
        cbid = event.getId();
    }
    @EventHandler
    private void onSaidBattle(SaidBattleEvent event) {
        chat.appendPlainText(String.format("<%s> %s", event.getUser(), event.getMessage()));
    }
    
    @EventHandler
    private void onLeftBattle(LeftBattleEvent event) {
        QListWidgetItem             i;
        
        if (event.getId() == cbid) {
            chat.appendPlainText(String.format("-!- %s has left the battle", event.getUser()));
            
            i = userToListWidget.get(event.getUser());
            users.removeItemWidget(i);
        }        
    }
    
    
    
    @EventHandler
    private void onClientBattleStatus(ClientBattleStatusEvent event) {
        
    }
    
    //public static void joinBattle(int bid) {
    //    getInstance().mwin.getLobbyService().joinBattle(bid);
    //}
    
    private void onEditBoxEnterPressed() {
        String          msg;
        
        msg = editBox.text();
        editBox.setText("");
        
        ls.sendBattleStatus(Integer.parseInt(msg), 0);
    }        
    
    @Override
    public String getTitle() {
        return "Battle";
    }
    
    public void onSplitterMove(int pos, int index) {
        _resizeEvent(width(), height());
    }

    public void _resizeEvent(int w, int h) {
        //hsplitter.setSizes(hsplitter.sizes());
        //System.out.printf("resize-event\n");
        //chat.resize(chatSurface.width(), chatSurface.height() - editBox.height());
        //editBox.move(0, chatSurface.height() - editBox.height());
        //editBox.resize(chatSurface.width(), editBox.height());
    }
    
    public void resizeEvent(int w, int h) {
        hsplitter.resize(w, h);
        _resizeEvent(w, h);
    }
    
    @Override
    public void resizeEvent(QResizeEvent event) {
        resizeEvent(width(), height());
    }
}
