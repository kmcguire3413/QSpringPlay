package com.kmcguire.slc;

import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QScrollBar;
import com.trolltech.qt.gui.QWidget;
import java.util.ArrayList;
import java.util.List;

public class QTaskArea extends QWidget {
    List<QWidget>               widgets;
    QWidget                     surface;
    QScrollBar                  scrollbar;
    
    public QTaskArea() {
        super();
        
        widgets = new ArrayList<QWidget>();
        surface = new QWidget(this);
        scrollbar = new QScrollBar(this);
        resizeEvent(width(), height());
        
        surface.setStyleSheet("background-color: #ff9999;");
    }
    
    public void addWidget(QWidget widget) {
        widgets.add(widget);
    }
    
    private void resizeEvent(int w, int h) {
        surface.move(0, 0);
        surface.resize(w - 20, h);
        scrollbar.move(w - 20, 0);
        scrollbar.resize(20, h);
    }
    
    @Override
    public void resizeEvent(QResizeEvent event) {
        resizeEvent(event.size().width(), event.size().height());
    }
}
