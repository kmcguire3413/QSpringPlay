package com.kmcguire.slc;

import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QWidget;

class BattlePanel extends QWidget {
    private int             id;
    private int             maxPlayers;
    private boolean         hasPass;
    private String          map;
    private String          title;
    private String          mod;
    private String          curMap;

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
        
        labelTitle = new QLabel(this);
        labelFrame = new QLabel(this);
        labelModMap = new QLabel(this);
        labelMap = new QLabel(this);
    }

    public QLabel getLabelFrame() {
        return labelFrame;
    }

    public QLabel getLabelMap() {
        return labelMap;
    }
}