package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.BattleClosedEvent;
import com.kmcguire.slc.LobbyService.BattleOpenedEvent;
import com.kmcguire.slc.LobbyService.EventHandler;
import com.kmcguire.slc.LobbyService.UpdateBattleInfoEvent;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QScrollBar;
import com.trolltech.qt.gui.QWidget;
import java.util.HashMap;
import java.util.Map;






class BattlePanel extends QWidget {
    private int             id;
    private int             type;
    private int             nat;
    private String          user;
    private String          host;
    private int             port;
    private int             maxPlayers;
    private boolean         hasPass;
    private int             rank;
    private long            hash;
    private String          map;
    private String          title;
    private String          mod;

    public boolean isHasPass() {
        return hasPass;
    }

    public void setHasPass(boolean hasPass) {
        this.hasPass = hasPass;
    }

    public long getHash() {
        return hash;
    }

    public void setHash(long hash) {
        this.hash = hash;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getNat() {
        return nat;
    }

    public void setNat(int nat) {
        this.nat = nat;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getSpecs() {
        return specs;
    }

    public void setSpecs(int specs) {
        this.specs = specs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    
    private QLabel                  labelTitle;
    private MultiplayerPanel        mp;
    
    public BattlePanel(
            MultiplayerPanel mp,
            int id, int type, int nat, 
            String user, String host, int port, 
            int maxPlayers, boolean hasPass, int rank, 
            long hash, String map, String title, 
            String mod, int specs, int panelWidth, int panelHeight) {
        this.mp = mp;
        this.id = id;
        this.type = type;
        this.nat = nat;
        this.user = user;
        this.host = host;
        this.port = port;
        this.maxPlayers = maxPlayers;
        this.hasPass = hasPass;
        this.rank = rank;
        this.hash = hash;
        this.map = map;
        this.title = title;
        this.mod = mod;
        this.specs = specs;
        
        resize(panelWidth, panelHeight);
        // add title
        labelTitle = new QLabel(this);
        labelTitle.move(0, 0);
        labelTitle.setText(title);
        // add mod.... add springver
        // add pictures of people
        
        // add map image on left
    }
    private int             specs;
}

public class MultiplayerPanel extends Panel {
    private MainWindow                  mwin;
    private Map<Integer, BattlePanel>   panels;
    private QWidget                     surface;
    private int                         yoffset;
    private QScrollBar                  scrollbar;
    
    private static final int            panelWidth;
    private static final int            panelHeight;
    
    static {
        panelWidth = 300;
        panelHeight = 125;
    }
    
    public MultiplayerPanel(MainWindow _mwin) {
        mwin = _mwin;
        
        panels = new HashMap<Integer, BattlePanel>();
        surface = new QWidget(this);
        
        scrollbar = new QScrollBar(surface);
        
        yoffset = 0;
        
        positionPanels();
        
        mwin.getLobbyService().registerForEvents(this);
    }
    
    @EventHandler
    private void onBattleOpened(BattleOpenedEvent e) {
        BattlePanel         bp;
        
        bp = new BattlePanel(
                this,
                e.getId(), e.getType(), e.getNat(), e.getUser(),
                e.getHost(), e.getPort(), e.getMaxPlayers(),
                e.isHasPass(), e.getRank(), e.getHash(),
                e.getMap(), e.getTitle(), e.getMod(),
                0,
                panelWidth, panelHeight
        );
        
        panels.put(bp.getId(), bp);
        bp.setParent(surface);
        positionPanels();
    }
    
    @EventHandler
    private void onBattleClosed(BattleClosedEvent event) {
        panels.remove(event.getId());
    }
    
    @EventHandler
    private void onUpdateBattleInfo(UpdateBattleInfoEvent event) {
        
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
        positionPanels();
    }
    
    public void resizeEvent(QResizeEvent event) {
        resizeEvent(width(), height());
    }
    
    public void scrollbarChanged(int value) {
        yoffset = -value;
        positionPanels();
    }
    
    private void positionPanels() {
        int         colcnt;
        int         colcur;
        int         rowcur;
        int         x;
        int         y;
        int         scrollMax;
        
        colcnt = (int)((surface.width() - scrollbar.width()) / panelWidth);
        
        if (colcnt > 0) {
            scrollMax = (int)Math.ceil(panels.values().size() / colcnt) * panelHeight;
            scrollbar.setMaximum(scrollMax);
        } else {
            scrollbar.setMaximum(0);
        }
        
        colcur = 0;
        rowcur = 0;
        
        for (BattlePanel bp : panels.values()) {
            if (colcur >= colcnt) {
                ++rowcur;
                colcur = 0;
            }
            
            x = colcur * panelWidth;
            y = -yoffset + (rowcur * panelHeight);
            
            bp.move(x, y);
            
            ++colcur;
        }
    }
}
