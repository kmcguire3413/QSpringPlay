package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.BattleClosedEvent;
import com.kmcguire.slc.LobbyService.BattleOpenedEvent;
import com.kmcguire.slc.LobbyService.EventHandler;
import com.kmcguire.slc.LobbyService.UpdateBattleInfoEvent;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QScrollBar;
import com.trolltech.qt.gui.QWidget;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

class Battle {
    public int          id;
    public int          maxPlayers;
    public boolean      hasPass;
    public String       map;            // changable
    public String       title;
    public String       mod;
    public Set<String>  players;
    public int          cntSpecs;          // changable
}


class MapUpdate {
    public QPixmap             image;
    public QLabel              label;

    public MapUpdate(QPixmap image, QLabel label) {
        this.image = image;
        this.label = label;
    }
}

public class MultiplayerPanel extends Panel {
    private MainWindow                  mwin;
    private QWidget                     surface;
    private int                         yoffset;
    private QScrollBar                  scrollbar;
    
    //
    private QTimer                      timer;
    private Set<MapUpdate>              mapUpdates;
    
    // all panels
    private Set<BattlePanel>            panels;
    // panels created for only internal use
    private Set<BattlePanel>            ipanels;
    // tracks all battles and information
    private Map<Integer, Battle>        battles;
    
    private static final QPixmap        frameNormal;
    private static final QPixmap        frameBattle;
    
    private static final int            panelWidth;
    private static final int            panelHeight;
            
    static {
        panelWidth = 300;
        panelHeight = 125;
        
        frameNormal = new QPixmap();
        frameBattle = new QPixmap();
        
        try {
            frameNormal.loadFromData(SpringLobbyClient.loadResource("images/battleframenormal.png"));
            frameBattle.loadFromData(SpringLobbyClient.loadResource("images/battleframebattle.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.printf("warning: was trying to load battle frame resources\n");
        }
    }
    
    public MultiplayerPanel(MainWindow _mwin) {
        mwin = _mwin;
    
        timer = new QTimer();
        timer.timeout.connect(this, "checkMapFetched()");
        timer.setInterval(1000);
        timer.start();
        mapUpdates = new LinkedHashSet<MapUpdate>();
        
        panels = new HashSet<BattlePanel>();
        ipanels = new HashSet<BattlePanel>();
        
        battles = new HashMap<Integer, Battle>();
        
        surface = new QWidget(this);
        
        scrollbar = new QScrollBar(surface);
        scrollbar.valueChanged.connect(this, "scrollbarChanged(int)");
        
        yoffset = 0;
        
        drawPanels();
        
        mwin.getLobbyService().registerForEvents(this);
    }
    
    /**
     * This is part of the code to rejoin results from the asynchronous
     * MapManager fetch code thus on interval this is executed and it 
     * takes any updates and applies them to the Qt widgets from this
     * main thread and not the asynchronous MapManager thread. See the
     * MapManager and see the updateBattlePanel method.
     */
    public void checkMapFetched() {
        Set<MapUpdate>      tmp;
        
        tmp = mapUpdates;
        mapUpdates = new LinkedHashSet<MapUpdate>();
        
        for (MapUpdate mu : mapUpdates) {
            mu.label.setPixmap(mu.image);
        }
    }
        
    @EventHandler
    private void onBattleOpened(BattleOpenedEvent event) {
        BattlePanel         bp;
        Battle              b;
        
        b = new Battle();
        b.hasPass = event.isHasPass();
        b.id = event.getId();
        b.map = event.getMap();
        b.maxPlayers = event.getMaxPlayers();
        b.mod = event.getMod();
        b.title = event.getTitle();
        battles.put(b.id, b);
        
        // create a battle panel for this widget
        bp = makeBattlePanel(event.getId());
        bp.setParent(surface);
        ipanels.add(bp);
        
        // otherwise the panel is going to be at 0:0 and
        // will be visible
        drawPanels();
    }
    
    @EventHandler
    private void onBattleClosed(BattleClosedEvent event) {
        // destroy battle panel for this widget
        for (BattlePanel bp : ipanels) {
            if (bp.getId() == event.getId()) {
                // need to dereference it from everything so that
                // it can be garbage collected so hopefully i am
                // doing all that needs to be done here
                ipanels.remove(bp);
                bp.setParent(null);
                break;
            }
        }
        
        // update the internal tracking of battles
        battles.remove(event.getId());
    }
    
    @EventHandler
    private void onBattleUsersChanged(BattleUsersChangedEvent event) {
        Battle          b;
        // update any battle panel for this battle
        
        b = battles.get(event.getBattleId());
        b.players = new HashSet(event.getPlayers());
        
        
        drawPanels();
    }
    
    @EventHandler
    private void onUpdateBattleInfo(UpdateBattleInfoEvent event) {
        // update any battle panel for this battle
        Battle          b;
        
        b = battles.get(event.getId());
        b.hasPass = event.isHasPass();
        b.map = event.getMap();
        
        drawPanels();
    }
    
    public void updateBattlePanel(BattlePanel bp) {
       QLabel           label;
       Battle           b;
       final QLabel     labelMap;
       QPixmap          image;
       
       b = battles.get(bp.getId());
       
       label = bp.getLabelTitle();
       label.setText(b.title);
       label.move(72, 0);
       label.show();
       label = bp.getLabelModMap();
       label.setText(String.format("%s [%s]", b.mod, b.map));
       label.move(72, 15);
       label.show();
       label = bp.getLabelFrame();
       label.setPixmap(frameNormal);
       label.move(0, 0);
       label.show();
       labelMap = bp.getLabelMap();
       labelMap.move(3, 3);
       labelMap.resize(62, 62);
       labelMap.show();
       if (bp.getCurMap() == null || bp.getCurMap().equals(bp.getMap())) {
           
           image = MapManager.getInstance().requestMinimap(bp.getMap(), new MapManagerCb() {
               @Override
               public void run(String mapName, QPixmap image) {
                   image = image.scaled(62, 62);
                   mapUpdates.add(new MapUpdate(image, labelMap));
               }
           });
           
           if (image != null) {
               image = image.scaled(62, 62);
               labelMap.setPixmap(image);
           }
           bp.setCurMap(bp.getMap());
       }
    }
    
    /**
     * This will return a reference to a battle panel which is a QWidget. The
     * panel will be automatically kept updated as information about the battle
     * changes.
     * @param bid               the battle ID
     * @return                  the battle panel QWidget reference
     */
    public BattlePanel makeBattlePanel(int bid) {
        BattlePanel             bp;
        Battle                  b;
        
        b = battles.get(bid);
        // id, maxPayers, hasPass, map, title, mod
        bp = new BattlePanel(
                b.id, b.maxPlayers, b.hasPass, 
                b.map, b.title, b.mod
        );
        
        updateBattlePanel(bp);
        return bp;
    }
    
    
    @Override
    public String getTitle() {
        return "Multiplayer";
    }
    
    private void resizeEvent(int w, int h) {
        surface.move(0, 0);
        surface.resize(w, h);
        scrollbar.resize(20, surface.height());
        scrollbar.move(surface.width() - scrollbar.width(), 0);
        drawPanels();
    }
    
    @Override
    public void resizeEvent(QResizeEvent event) {
        resizeEvent(width(), height());
    }
    
    public void scrollbarChanged(int value) {
        yoffset = -value;
        drawPanels();
    }
    
    private void drawPanels() {
        int         colcnt;
        int         colcur;
        int         rowcur;
        int         x;
        int         y;
        int         scrollMax;
        
        colcnt = (int)((surface.width() - scrollbar.width()) / panelWidth);
        
        if (colcnt > 0) {
            scrollMax = (int)Math.ceil(ipanels.size() / colcnt) * panelHeight;
            scrollbar.setMaximum(scrollMax);
        } else {
            scrollbar.setMaximum(0);
        }
        
        colcur = 0;
        rowcur = 0;
        
        for (BattlePanel bp : ipanels) {
            if (colcur >= colcnt) {
                ++rowcur;
                colcur = 0;
            }
            
            x = colcur * panelWidth;
            y = yoffset + (rowcur * panelHeight);
            
            bp.move(x, y);
            bp.show();
            ++colcur;
        }
    }
}
