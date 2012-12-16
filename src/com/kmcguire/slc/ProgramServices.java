package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.LobbyService;
import com.trolltech.qt.gui.QIcon;
import java.util.Set;

/**
 * I just needed a way to abstract some of the services that were being provided
 * by MainWindow because it is actually more of a Widget/UI element even though it
 * does provide services. 
 * 
 * - provides battle list services.
 *      A list of users in a specific battle. This prevents each panel, plugin,
 *      or object having to re-implement the tracking logic each time they need
 *      it. I may move this to SpringLobby.
 * - provides flag icon services
 *      Provides automatic fetching if flag icon resources and caches flag icon
 *      resources to reduce memory consumption and thrashing.
 * - provides LobbyServer getter
 *      Gives the LobbyService interface which is required to register for lobby
 *      protocol events and perform actions on the lobby state/connection.
 * - provides method to add panels to the program
 *      This abstracts the idea of the panel from the MainWindow so now there could
 *      be any type of object, style, or concept.
 * @author kmcguire
 */
public interface ProgramServices {
    public QIcon getFlagIcon(String code);
    public Set<String> getBattleList(int id);
    public LobbyService getLobbyService();
    public void addPanel(Panel panel);
}
