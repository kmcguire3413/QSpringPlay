package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.BattleStatus;
import com.kmcguire.slc.LobbyService.ChannelTopicEvent;
import com.kmcguire.slc.LobbyService.ClientBattleStatusEvent;
import com.kmcguire.slc.LobbyService.ClientsEvent;
import com.kmcguire.slc.LobbyService.EventHandler;
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

/**
 * This is the main panel/widget for the current battle room. There can only
 * exist one of these technically. However, if the lobby server supported a
 * user being in multiple battles then this could be changed to support that.
 * @author kmcguire
 */
public class BattleRoomPanel extends Panel {
    private MainWindow                           mwin;
    private static BattleRoomPanel               instance;
    
    private int                            cbid;
    private QListWidget                    users;
    private QPlainTextEdit                 chat;
    private QSplitter                      hsplitter;
    private QSplitter                      vsplitter;
    private QLineEdit                      editBox;
    private QWidget                        chatSurface;
    private LobbyService                   ls;

    private Map<String, QListWidgetItem>   userToListWidget;
    
    public BattleRoomPanel(MainWindow mwin) throws LobbyGeneralException {
        if (instance != null) {
            throw new LobbyGeneralException("Only one instance can be created!");
        }
        
        instance = this;
        this.mwin = mwin;
        
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
        
        mwin.getLobbyService().registerForEvents(this);
        
        ls = mwin.getLobbyService();
        
        userToListWidget = new HashMap<String, QListWidgetItem>();
    }
    
    public static BattleRoomPanel getInstance() {
        return instance;
    }
    
    public static void joinBattle(int bid) {
        getInstance().chat.appendPlainText(String.format("-!- joined battle %d", bid));
        getInstance().mwin.getLobbyService().joinBattle(bid);
    }
    
    @EventHandler
    private void onRequestBattleStatus(RequestBattleStatusEvent event) {
        BattleStatus            bs;
        
        bs = new BattleStatus(0);
        bs.setReady(false);
        bs.setPlayer(false);
        bs.setSync(1);
        
        System.out.printf("sending battle status %d\n", bs.getStatus());
        ls.sendBattleStatus(bs.getStatus(), 0);
    }
    
    @EventHandler
    private void onJoinedBattle(JoinedBattleEvent event) {
        QListWidgetItem         i;
        
        if (event.getId() == cbid) {
            chat.appendPlainText(String.format("-!- %s has joined the battle", event.getUser()));
            
            i = new QListWidgetItem();
            userToListWidget.put(event.getUser(), i);
            
            i.setText(event.getUser());
            
            users.addItem(i);
        }
    }
    
    @EventHandler
    private void onSaidBattle(SaidBattleEvent event) {
        chat.appendHtml(String.format("<b>[</b>%s<b>]</b>: %s", event.getUser(), event.getMessage()));
    }
    
    @EventHandler
    private void onLeftBattle(LeftBattleEvent event) {
        QListWidgetItem             i;
        
        if (event.getId() == cbid) {
            chat.insertPlainText(String.format("-!- %s has left the battle", event.getUser()));
            
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
        //mwin.getLobbyService().sayChannel(channel, msg);
    }
        
    @EventHandler
    private void onClients(ClientsEvent event) {
        QListWidgetItem         item;
        QIcon                   icon;
        LobbyUser               lu;
        
        //if (event.getChannel().equals(channel)) {
        //    for (String user : event.getClients()) {
                //users.addItem(user);
                //lu = mwin.getLobbyUser(user);
                //icon = mwin.getFlagIcon(lu.getCountry());
                //item = new QListWidgetItem();
                //item.setIcon(icon);
                //item.setText(user);
                //users.addItem(item);
        //    }
        //}
    }
    
    @EventHandler
    private void onJoined(JoinedEvent event) {
        //if (event.getChannel().equals(channel)) {
        //    users.addItem(event.getUser());
        //}
    }
    
    @EventHandler
    private void onLeft(LeftEvent event) {
        //QListWidgetItem         item;
        //if (event.getChannel().equals(channel)) {
        //    for (int i = 0; i < users.count(); ++i) {
        //        item = users.item(i);
        //        if (item.text().equals(event.getUser())) {
        //            users.removeItemWidget(item);
        //            return;
        //        }
        //    }                    
        //}
    }
    
    @EventHandler
    private void onChannelTopic(ChannelTopicEvent event) {
        String          line;
        
        //if (event.getChannel().equals(channel)) {
        //    line = String.format("-!- Topic for %s: %s", event.getChannel(), event.getTopic());
        //    chat.appendPlainText(line);
        //    line = String.format("-!- Topic set by %s [%s]", event.getUser(), event.getPos());
        //    chat.appendPlainText(line);
        //}    
    }
    
    @EventHandler
    private void onSaid(SaidEvent event) {
        String          line;
        
        //if (event.getChannel().equals(channel)) {
        //    line = String.format("[%s]: %s", event.getUser(), event.getMessage());
        //    chat.appendPlainText(line);
        //}
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
