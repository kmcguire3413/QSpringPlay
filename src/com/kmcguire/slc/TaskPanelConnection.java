/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kmcguire.slc;

import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QWidget;

/**
 * This provides a task panel which will display connection information, and
 * allow the connection to be closed.
 * @author kmcguire
 */
public class TaskPanelConnection extends QTaskPanel {
    private MainWindow                  mwin;
    private QWidget                     surface;
    
    public TaskPanelConnection(MainWindow _mwin) {
        mwin = _mwin;
        surface = new QWidget(this);
    }
    
    @Override
    public void resizeEvent(QResizeEvent event) {
        surface.resize(this.width(), this.height());
    }
}
