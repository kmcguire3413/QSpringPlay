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
        
        scrollbar.valueChanged.connect(this, "scrollbarChanged(int)");
        
        resizeEvent(width(), height());
        
        surface.setStyleSheet("background-color: #ff9999;");
    }
    
    public void scrollbarChanged(int value) {
        int     y;
        
        y = -value;
        for (QWidget widget : widgets) {
            widget.move(0, y);
            y += widget.height();
        }
    }
    
    public void addWidget(QWidget widget) {
        widget.setParent(this);
        widgets.add(widget);
    }
    
    private void resizeEvent(int w, int h) {
        int     th;
        
        surface.move(0, 0);
        surface.resize(w - 20, h);
        scrollbar.move(w - 20, 0);
        scrollbar.resize(20, h);
        
        th = 0;
        for (QWidget widget : widgets) {
            th += widget.height();
        }
        
        if (surface.height() >= th) {
            scrollbar.setMaximum(0);
        } else {
            scrollbar.setMaximum(th - surface.height());
        }
        
        scrollbarChanged(scrollbar.value());
    }
    
    @Override
    public void resizeEvent(QResizeEvent event) {
        resizeEvent(event.size().width(), event.size().height());
    }
}
