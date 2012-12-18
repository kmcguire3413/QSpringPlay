package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.BattleClosedEvent;
import com.kmcguire.slc.LobbyService.BattleOpenedEvent;
import com.kmcguire.slc.LobbyService.EventHandler;
import com.kmcguire.slc.LobbyService.UpdateBattleInfoEvent;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.core.Qt.CheckState;
import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QScrollBar;
import com.trolltech.qt.gui.QWidget;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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

public class MultiplayerPanelZkStyle extends Panel {
    private ProgramServices             services;
    private QWidget                     surface;
    private int                         yoffset;
    private QScrollBar                  scrollbar;
    
    //
    private QTimer                      timer;
    private Set<MapUpdate>              mapUpdates;
    //
    private QCheckBox                   checkboxShowEmpty;
    private QCheckBox                   checkboxShowFull;
    private QCheckBox                   checkboxShowPass;
    private QLineEdit                   lineditSearch;
    private QLabel                      labelSearch;
    private QGridLayout                 gridLayout;
    private QWidget                     controls;
    
    //
    private String                      battleFilter;
    
    // all panels
    private Set<BattlePanel>            panels;
    // panels created for only internal use
    private Set<BattlePanel>            ipanels;
    // tracks all battles and information
    private Map<Integer, Battle>        battles;
    
    private static final QPixmap        frameNormal;
    private static final QPixmap        frameBattle;
    
    public static final int             panelWidth;
    public static final int             panelHeight;
    private static final int            surfaceY;
    
    private static final QPixmap        person;
    private static final QPixmap        noperson;
    
    private static MultiplayerPanelZkStyle        instance;
    
    static {
        panelWidth = 300;
        panelHeight = 72;
        
        instance = null;
        
        surfaceY = 50;
        
        frameNormal = new QPixmap();
        frameBattle = new QPixmap();
        person = new QPixmap();
        noperson = new QPixmap();
        
        try {
            frameNormal.loadFromData(SpringLobbyClient.loadResource("images/battleframenormal.png"));
            frameBattle.loadFromData(SpringLobbyClient.loadResource("images/battleframebattle.png"));
            person.loadFromData(SpringLobbyClient.loadResource("images/person.png"));
            noperson.loadFromData(SpringLobbyClient.loadResource("images/noperson.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.printf("warning: was trying to load resources\n");
        }
    }
    
    public static MultiplayerPanelZkStyle getInstance() {
        return instance;
    }
    
    public static MultiplayerPanelZkStyle createInstance(ProgramServices services) {
        if (instance == null) {
            instance = new MultiplayerPanelZkStyle(services);
        }
        
        return instance;
    }
    
    private MultiplayerPanelZkStyle(ProgramServices _services) {
        services = _services;
    
        timer = new QTimer();
        timer.timeout.connect(this, "checkMapFetched()");
        timer.setInterval(1000);
        timer.start();
        mapUpdates = new LinkedHashSet<MapUpdate>();
        
        panels = new HashSet<BattlePanel>();
        ipanels = new HashSet<BattlePanel>();
        
        battles = new HashMap<Integer, Battle>();
        
        controls = new QWidget(this);
        checkboxShowEmpty = new QCheckBox(controls);
        checkboxShowEmpty.setText("Show Empty");
        checkboxShowEmpty.setCheckState(CheckState.Checked);
        checkboxShowEmpty.show();
        checkboxShowFull = new QCheckBox(controls);
        checkboxShowFull.setText("Show Full");
        checkboxShowFull.setCheckState(CheckState.Checked);
        checkboxShowFull.show();
        checkboxShowPass = new QCheckBox(controls);
        checkboxShowPass.setText("Password?");
        checkboxShowPass.setCheckState(CheckState.Checked);
        checkboxShowPass.show();
        lineditSearch = new QLineEdit(controls);
        lineditSearch.show();
        labelSearch = new QLabel(controls);
        labelSearch.setText("Search:");
        labelSearch.show();
        
        checkboxShowFull.stateChanged.connect(this, "checkboxChanged(int)");
        checkboxShowPass.stateChanged.connect(this, "checkboxChanged(int)");
        checkboxShowEmpty.stateChanged.connect(this, "checkboxChanged(int)");
        lineditSearch.textChanged.connect(this, "searchChanged(String)");
        battleFilter = "";
        
        gridLayout = new QGridLayout();
        gridLayout.addWidget(checkboxShowEmpty, 0, 0);
        gridLayout.addWidget(checkboxShowFull, 0, 1);
        gridLayout.addWidget(checkboxShowPass, 0, 2);
        gridLayout.addWidget(labelSearch, 0, 3);
        gridLayout.addWidget(lineditSearch, 0, 4);
        
        controls.setLayout(gridLayout);
        controls.show();
        
        
        surface = new QWidget(this);
        
        scrollbar = new QScrollBar(surface);
        scrollbar.valueChanged.connect(this, "scrollbarChanged(int)");
        
        yoffset = 0;
        
        drawPanels();
        
        services.getLobbyService().registerForEvents(this);
    }
    
    public void checkboxChanged(int state) {
        drawPanels();
    }
    
    public void searchChanged(String text) {
        battleFilter = text;
        drawPanels();
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
        
        for (MapUpdate mu : tmp) {
            mu.label.setPixmap(mu.image);
        }
    }
        
    @EventHandler
    private void onBattleOpened(BattleOpenedEvent event) {
        BattlePanel         bp;
        Battle              b;
        final int           bid;
        
        b = new Battle();
        b.hasPass = event.isHasPass();
        b.id = event.getId();
        b.map = event.getMap();
        b.maxPlayers = event.getMaxPlayers();
        b.mod = event.getMod();
        b.title = event.getTitle();
        battles.put(b.id, b);
        
        // create a battle panel for this widget
        bp = makeBattlePanel(event.getId(), true);
        bp.setParent(surface);
        bp.show();
        
        bid = b.id;
        bp.setCb(new BattlePanelCb() {
            @Override
            public void onMouseRelease(QMouseEvent event) {
                BattleRoomPanel.getInstance().joinBattle(bid);
            }
        });
        
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
    
    private BattlePanel getPanel(int bid) {
        for (BattlePanel bp : ipanels) {
            if (bp.getId() == bid) {
                return bp;
            }
        }
        
        for (BattlePanel bp : panels) {
            if (bp.getId() == bid) {
                return bp;
            }
        }
        return null;
    }
    
    @EventHandler
    private void onBattleUsersChanged(BattleUsersChangedEvent event) {
        onBattleUsersChanged(event.getBattleId(), event.getPlayers());
    }
    
    private void onBattleUsersChanged(int bid, Set<String> playerList) {
        Battle              b;
        QLabel[]            spots;
        BattlePanel         bp;
        QLabel              spot;
        final int           gx, gy;
        final int           colcnt;
        Iterator<String>    i;
        String              user;
        
        // update any battle panel for this battle
        
        b = battles.get(bid);
        if (playerList == null) {
            b.players = new HashSet<String>();
        } else {
            b.players = new HashSet(playerList);
        }
        
        // redo the player list for any battle panel representing this battle
        bp = getPanel(b.id);
        
        // let it throw some errors at least we shall know
        // that something is wrong
        //if (bp == null) {
        //    return;
        //}
        
        
        
        gx = 72;
        gy = 30;
        colcnt = 13;
        
        spots = bp.getLabelPlayers();
        i = b.players.iterator();
        
        for (int y = 0; y < 2; ++y) {
            for (int x = 0; x < colcnt; ++x) {
                if (y * colcnt + x >= spots.length) {
                    break;
                }
                if (i.hasNext()) {
                    user = i.next();
                } else {
                    user = null;
                }
                spot = spots[y * colcnt + x];
                spot.move(gx + x * 16, gy + y * 17);
                spot.resize(16, 17);
                spot.show();
                if (user == null) {
                    spot.setPixmap(noperson);
                } else {
                    spot.setPixmap(person);
                }
            }
        }
        
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
       
       onBattleUsersChanged(b.id, b.players);
    }
    
    /**
     * This is the externally called service method.
     * @param bid               the battle id (signed 32-bit integer)
     * @return                  reference to a BattlePanel Qt widget
     */
    public BattlePanel makeBattlePanel(int bid) {
        return makeBattlePanel(bid, false);
    }
    
    /**
     * This will return a reference to a battle panel which is a QWidget. The
     * panel will be automatically kept updated as information about the battle
     * changes.
     * @param bid               the battle ID
     * @return                  the battle panel QWidget reference
     */
    private BattlePanel makeBattlePanel(int bid, boolean internal) {
        BattlePanel             bp;
        Battle                  b;
        
        b = battles.get(bid);
        // id, maxPayers, hasPass, map, title, mod
        bp = new BattlePanel(
                b.id, b.maxPlayers, b.hasPass, 
                b.map, b.title, b.mod
        );
        if (internal) {
            ipanels.add(bp);
        } else {
            panels.add(bp);
        }
        
        updateBattlePanel(bp);
        return bp;
    }
    
    /**
     * There needed to exist a way to ensure the battle panel is
     * properly destroyed and garbage collected. I may want to come
     * back and use some type of weak reference containers for ipanels
     * and panels, but I have never done that so just code it later.
     */
    public void destroyBattlePanel(BattlePanel bp) {
        if (ipanels.contains(bp)) {
            ipanels.remove(bp);
        }
        
        if (panels.contains(bp)) {
            panels.remove(bp);
        }
        
        bp.setParent(null);
    }
    
    @Override
    public String getTitle() {
        return "Multiplayer";
    }
    
    private void resizeEvent(int w, int h) {
        controls.move(0, 0);
        controls.resize(w, surfaceY);
        surface.move(0, surfaceY);
        surface.resize(w, h - surfaceY);
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
        Battle      b;
        
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
            
            b = battles.get(bp.getId());
            
            if (
                    (b.players.size() > 0 || (b != null && b.players.isEmpty() && checkboxShowEmpty.isChecked())) &&
                    ((b.players.size() < bp.getMaxPlayers()) || (b != null && b.players.size() == bp.getMaxPlayers() && checkboxShowFull.isChecked())) &&
                    ((!bp.isHasPass()) || (bp.isHasPass() && checkboxShowPass.isChecked())) && 
                    (battleFilter.length() < 1 ||
                    bp.getTitle().indexOf(battleFilter) > -1 ||
                    bp.getMod().indexOf(battleFilter) > -1 ||
                    bp.getMap().indexOf(battleFilter) > -1)
            ) {
                bp.setParent(surface);
                bp.move(x, y);
                bp.show();
                ++colcur;
            } else {
                bp.hide();
            }
        }
    }
}
