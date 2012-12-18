package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.LobbyService;
import com.trolltech.qt.gui.QIcon;
import java.util.Set;

/**
 * I just needed a way to abstract some of the services that were being provided
 * by MainWindow because it is actually more of a Widget/UI element even though it
 * does provide services. 
 * 
 * It basically provides anything that is not absolutely specific to MainWindow,
 * which includes like 99 percent of most stuff.
 * @author kmcguire
 */
public interface ProgramServices {
    public QIcon getFlagIcon(String code);
    public Set<String> getBattleList(int id);
    public LobbyService getLobbyService();
    public void addPanel(Panel panel);
    public QTaskArea getTaskArea();
    public Battle getBattleInfo(int bid);
}
