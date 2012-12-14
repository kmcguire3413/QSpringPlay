package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.BattleClosedEvent;
import com.kmcguire.slc.LobbyService.BattleOpenedEvent;
import com.kmcguire.slc.LobbyService.EventHandler;
import com.kmcguire.slc.LobbyService.UpdateBattleInfoEvent;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QScrollBar;
import com.trolltech.qt.gui.QWidget;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


class BattlePanel extends QWidget {
    private int             id;
    private int             maxPlayers;
    private boolean         hasPass;
    private String          map;
    private String          title;
    private String          mod;
    
    private QLabel          labelTitle;
    private QLabel          labelModMap;
    private QLabel          labelFrame;
    private QLabel          labelMap;

    public boolean isHasPass() {
        return hasPass;
    }

    public void setHasPass(boolean hasPass) {
        this.hasPass = hasPass;
    }

    public QLabel getLabelModMap() {
        return labelModMap;
    }

    public void setLabelModMap(QLabel labelModMap) {
        this.labelModMap = labelModMap;
    }

    public QLabel getLabelTitle() {
        return labelTitle;
    }

    public void setLabelTitle(QLabel labelTitle) {
        this.labelTitle = labelTitle;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public BattlePanel(int id, int maxPlayers, boolean hasPass, String map, String title, String mod) {
        this.id = id;
        this.maxPlayers = maxPlayers;
        this.hasPass = hasPass;
        this.map = map;
        this.title = title;
        this.mod = mod;
        
        labelTitle = new QLabel();
        labelFrame = new QLabel();
        labelModMap = new QLabel();
        labelMap = new QLabel();
    }

    public QLabel getLabelFrame() {
        return labelFrame;
    }

    public QLabel getLabelMap() {
        return labelMap;
    }
}

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

public class MultiplayerPanel extends Panel {
    private MainWindow                  mwin;
    private QWidget                     surface;
    private int                         yoffset;
    private QScrollBar                  scrollbar;
    private QTimer                      timer;
    
    // all panels
    private Set<BattlePanel>            panels;
    // panels created for only internal use
    private Set<BattlePanel>            ipanels;
    // tracks all battles and information
    private Map<Integer, Battle>        battles;
    
    private static final int            panelWidth;
    private static final int            panelHeight;
            
    static {
        panelWidth = 300;
        panelHeight = 125;
    }
    
    public MultiplayerPanel(MainWindow _mwin) {
        mwin = _mwin;
    
        timer = new QTimer();
        timer.timeout.connect(this, "checkMapFetched()");
        timer.setInterval(1000);
        timer.start();
        
        panels = new HashSet<BattlePanel>();
        
        surface = new QWidget(this);
        
        scrollbar = new QScrollBar(surface);
        scrollbar.valueChanged.connect(this, "scrollbarChanged(int)");
        
        yoffset = 0;
        
        drawPanels();
        
        mwin.getLobbyService().registerForEvents(this);
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
        
        // create a battle panel for this widget
        bp = makeBattlePanel(event.getId());
        ipanels.add(bp);
    }
    
    @EventHandler
    private void onBattleClosed(BattleClosedEvent event) {
        // destroy battle panel for this widget
        for (BattlePanel bp : ipanels) {
            if (bp.getId() == event.getId()) {
                ipanels.remove(bp);
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
       
       b = battles.get(bp.getId());
       
       label = bp.getLabelTitle();
       label.setText(b.title);
       label = bp.getLabelModMap();
       label.setText(String.format("%s [%s]", b.mod, b.map));
       label = bp.getLabelFrame();
       
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
            scrollMax = (int)Math.ceil(panels.size() / colcnt) * panelHeight;
            scrollbar.setMaximum(scrollMax);
        } else {
            scrollbar.setMaximum(0);
        }
        
        colcur = 0;
        rowcur = 0;
        
        for (BattlePanel bp : panels) {
            if (colcur >= colcnt) {
                ++rowcur;
                colcur = 0;
            }
            
            x = colcur * panelWidth;
            y = yoffset + (rowcur * panelHeight);
            
            bp.move(x, y);
            ++colcur;
        }
    }
}
