package com.kmcguire.slc;

import com.trolltech.qt.gui.QFont;
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
    
    private static QPixmap        frameNormal;
    private static QPixmap        frameBattle;    
    
    private ProgramServices             services;
    
    static {
        frameNormal = new QPixmap();
        frameBattle = new QPixmap();
        
        try {
            frameNormal.loadFromData(SpringLobbyClient.loadResource("images/battleframenormal.png"));
            frameBattle.loadFromData(SpringLobbyClient.loadResource("images/battleframebattle.png"));
            
            frameNormal = frameNormal.scaled(30, 30);
            frameBattle = frameBattle.scaled(30, 30);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.printf("warning: was trying to load resources\n");
        }
    }
    
    public TaskPanelBattle(ProgramServices _services) {
        surface = new QWidget(this);
        
        services = _services;
        
        resize(10, 40);
        
        setFrameShadow(Shadow.Raised);
        setFrameShape(Shape.WinPanel);
        
        labelTitle = new QLabel(this);
        mapFrame = new QLabel(this);
        mapImage = new QLabel(mapFrame);
        labelTitle.show();
        mapFrame.show();
        mapImage.show();
    }

    public void configureForBattle(int _id) {
        Battle              b;
        QPixmap             img;
        
        b = services.getBattleInfo(_id);
        
        img = MapManager.getInstance().requestMinimap(b.getMap(), null);
        img = img.scaled(28, 28);
        
        mapFrame.move(5, 5);
        mapImage.move(1, 1);
        mapFrame.resize(30, 30);
        mapImage.resize(28, 28);
        mapFrame.setPixmap(frameNormal);
        mapImage.setPixmap(img);
        
        labelTitle.move(35, 5);
        labelTitle.setFont(new QFont("monospace", 14));
        labelTitle.setText(b.getTitle());
        labelTitle.resize(surface.width() - labelTitle.pos().x(), 25);
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
