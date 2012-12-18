package com.kmcguire.slc;

import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QWidget;

public class TaskPanelBattle extends QTaskPanel {
    private QWidget                 surface;
    private int                     id;
    private BattlePanel             bp;
    
    public TaskPanelBattle(ProgramServices services) {
        surface = new QWidget(this);
        
        resize(10, 80);
        
        setFrameShadow(Shadow.Raised);
        setFrameShape(Shape.WinPanel);
    }

    public void configureForBattle(int _id) {
        MultiplayerPanelZkStyle     mp;
        BattlePanel                 nbp;
        
        id = _id;
        
        mp = MultiplayerPanelZkStyle.getInstance();
        
        if (bp != null) {
            mp.destroyBattlePanel(bp);
        }
        
        nbp = mp.makeBattlePanel(_id);
        
        nbp.setParent(surface);
        nbp.move(0, 0);
        nbp.show();
        
        bp = nbp;
    }
    
    @Override
    public void resizeEvent(QResizeEvent event) {
        surface.resize(this.width(), this.height());
    }
}
