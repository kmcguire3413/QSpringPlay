package com.kmcguire.slc;

import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QWidget;
import java.io.IOException;

public class TaskPanelBattle extends QTaskPanel {
    private QWidget                     surface;
    private int                         id;
    private BattlePanel                 bp;
    
    private QLabel                      mapFrame;
    private QLabel                      mapImage;
    private QLabel                      labelTitle;
    
    private static final QPixmap        frameNormal;
    private static final QPixmap        frameBattle;    
    
    private ProgramServices             services;
    
    static {
        frameNormal = new QPixmap();
        frameBattle = new QPixmap();
        
        try {
            frameNormal.loadFromData(SpringLobbyClient.loadResource("images/battleframenormal.png"));
            frameBattle.loadFromData(SpringLobbyClient.loadResource("images/battleframebattle.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.printf("warning: was trying to load resources\n");
        }
    }
    
    public TaskPanelBattle(ProgramServices _services) {
        surface = new QWidget(this);
        
        services = _services;
        
        resize(10, 80);
        
        setFrameShadow(Shadow.Raised);
        setFrameShape(Shape.WinPanel);
        
        labelTitle = new QLabel(this);
        mapFrame = new QLabel(this);
        mapImage = new QLabel(this);
    }

    public void configureForBattle(int _id) {
        
        mapFrame.setPixmap(frameNormal);
        
        MapManager.getInstance().requestMinimap(null, null);
        
        
        
        //MultiplayerPanelZkStyle     mp;
        //BattlePanel                 nbp;
        
        //id = _id;
        
        //mp = MultiplayerPanelZkStyle.getInstance();
        
        //if (bp != null) {
        //    mp.destroyBattlePanel(bp);
        //}
        
        //nbp = mp.makeBattlePanel(_id);
        
        //nbp.setParent(surface);
        //nbp.move(0, 0);
        //nbp.show();
        
        //bp = nbp;
    }
    
    @Override
    public void resizeEvent(QResizeEvent event) {
        surface.resize(this.width(), this.height());
    }
}
