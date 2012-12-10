package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.ChannelTopicEvent;
import com.kmcguire.slc.LobbyService.ClientsEvent;
import com.kmcguire.slc.LobbyService.EventHandler;
import com.kmcguire.slc.LobbyService.JoinedEvent;
import com.kmcguire.slc.LobbyService.LeftEvent;
import com.kmcguire.slc.LobbyService.SaidEvent;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QFrame;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QListWidget;
import com.trolltech.qt.gui.QListWidgetItem;
import com.trolltech.qt.gui.QPlainTextEdit;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QSplitter;
import com.trolltech.qt.gui.QWidget;
import java.util.List;

public class ChatPanel extends Panel {
    private String                         channel;
    private MainWindow                     mwin;
    private QListWidget                    users;
    private QPlainTextEdit                 chat;
    private QSplitter                      hsplitter;
    private QSplitter                      vsplitter;
    private QLineEdit                      editBox;
    private QWidget                        chatSurface;
    
    public ChatPanel(MainWindow mwin, String channel) {
        this.mwin = mwin;
        this.channel = channel;
        
        //surface = new QWidget(this);
        //surface.setStyleSheet("background-color: #ffff33;");
        //surface.show();
        
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
    }
    
    private void onEditBoxEnterPressed() {
        String          msg;
        
        msg = editBox.text();
        editBox.setText("");
        
        mwin.getLobbyService().sayChannel(channel, msg);
    }
    
    @EventHandler
    private void onClients(ClientsEvent event) {
        if (event.getChannel().equals(channel)) {
            for (String user : event.getClients()) {
                users.addItem(user);
            }
        }
    }
    
    @EventHandler
    private void onJoined(JoinedEvent event) {
        if (event.getChannel().equals(channel)) {
            users.addItem(event.getUser());
        }
    }
    
    @EventHandler
    private void onLeft(LeftEvent event) {
        QListWidgetItem         item;
        if (event.getChannel().equals(channel)) {
            for (int i = 0; i < users.count(); ++i) {
                item = users.item(i);
                if (item.text().equals(event.getUser())) {
                    users.removeItemWidget(item);
                    return;
                }
            }                    
        }
    }
    
    @EventHandler
    private void onChannelTopic(ChannelTopicEvent event) {
        String          line;
        
        if (event.getChannel().equals(channel)) {
            line = String.format("-!- Topic for %s: %s", event.getChannel(), event.getTopic());
            chat.appendPlainText(line);
            line = String.format("-!- Topic set by %s [%s]", event.getUser(), event.getPos());
            chat.appendPlainText(line);
        }    
    }
    
    @EventHandler
    private void onSaid(SaidEvent event) {
        String          line;
        
        if (event.getChannel().equals(channel)) {
            line = String.format("[%s]: %s", event.getUser(), event.getMessage());
            chat.appendPlainText(line);
        }
    }
    
    @Override
    public String getTitle() {
        return String.format("#%s", channel);
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
