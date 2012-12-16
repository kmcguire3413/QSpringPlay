package com.kmcguire.slc;

import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPaintEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QWidget;

/**
 * This is a fairly odd class because it really does not do any
 * work as far as implementation. It does create some widgets and
 * prevents the widget instances from being modified in most cases. 
 * 
 * This object however despite its weird design represents a battle
 * panel which is just a rectangular widget of fixed width and height
 * that displays information about a battle.
 * 
 * The MultiplayerPanel uses a BattlePanel for each battle and displays
 * them in a grid like fashion, but external services can also call
 * MultiplayerPanel and request a BattlePanel. The BattlePanel they
 * get will be unique but will be updated by MultiplayerPanel.
 * 
 * Thus the real implementation is in MultiplayerPanel and this object
 * is more like a C structure with some initialization code and controlled
 * access to members.
 * @author kmcguire
 */
public class BattlePanel extends QWidget {
    private int             id;
    private int             maxPlayers;
    private boolean         hasPass;
    private String          map;
    private String          title;
    private String          mod;
    private String          curMap;
    private BattlePanelCb   cb;

    public BattlePanelCb getCb() {
        return cb;
    }

    public void setCb(BattlePanelCb cb) {
        this.cb = cb;
    }
    
    @Override
    public void mouseMoveEvent(QMouseEvent event) {
        if (cb == null)
            return;
        cb.onMouseDoubleClick(event);
    }
    
    @Override
    public void mousePressEvent(QMouseEvent event) {
        if (cb == null)
            return;
        cb.onMousePress(event);
    }
    
    @Override
    public void mouseReleaseEvent(QMouseEvent event) {
        if (cb == null)
            return;
        cb.onMouseRelease(event);
    }
    
    @Override
    public void mouseDoubleClickEvent(QMouseEvent event) {
        if (cb == null)
            return;
        cb.onMouseDoubleClick(event);
    }

    public String getCurMap() {
        return curMap;
    }

    public void setCurMap(String curMap) {
        this.curMap = curMap;
    }
    
    private QLabel          labelTitle;
    private QLabel          labelModMap;
    private QLabel          labelFrame;
    private QLabel          labelMap;
    private QLabel[]        labelPlayers;

    public QLabel[] getLabelPlayers() {
        return labelPlayers;
    }

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

    /**
     * Yeah I sort of create all the widgets here but manipulate them externally
     * to the battle panel. I know it seems a little weird of a design but I guess
     * this is just what ended up happening. I am not happy with this design, but
     * I got to move forward with the implementation, and do a rewrite later.
     * @param id                    battle identifier 32-bit signed integer
     * @param maxPlayers            maximum players in this battle
     * @param hasPass               does battle have a password
     * @param map                   what map is the battle using
     * @param title                 the title of the battle
     * @param mod                   what mod is the battle using
     */
    public BattlePanel(int id, int maxPlayers, boolean hasPass, String map, String title, String mod) {
        this.id = id;
        this.maxPlayers = maxPlayers;
        this.hasPass = hasPass;
        this.map = map;
        this.title = title;
        this.mod = mod;
        
        labelTitle = new QLabel(this);
        labelFrame = new QLabel(this);
        labelModMap = new QLabel(this);
        labelMap = new QLabel(this);
        labelPlayers = new QLabel[maxPlayers];
        
        for (int x = 0; x < maxPlayers; ++x) {
            labelPlayers[x] = new QLabel(this);
        }
        
        resize(MultiplayerPanelZkStyle.panelWidth, MultiplayerPanelZkStyle.panelHeight);
    }
    
    /**
     * I was thinking about rendering the player icons onto the panel here
     * but its going to go against the spirit of doing mostly everything in
     * the MultiplayerPanel like I am already doing. So this is just an 
     * orphaned method not being used.
     * @param event 
     */
    //@Override
    //public void paintEvent(QPaintEvent event) {
        //QPainter            p;
        //p = new QPainter(this);
    //}

    public QLabel getLabelFrame() {
        return labelFrame;
    }

    public QLabel getLabelMap() {
        return labelMap;
    }
}