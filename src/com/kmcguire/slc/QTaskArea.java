package com.kmcguire.slc;

import com.trolltech.qt.gui.QFrame;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QScrollBar;
import com.trolltech.qt.gui.QWidget;
import java.util.ArrayList;
import java.util.List;

public class QTaskArea extends QFrame {
    List<QTaskPanel>            widgets;
    QWidget                     surface;
    QScrollBar                  scrollbar;
    
    public QTaskArea() {
        super();
        
        widgets = new ArrayList<QTaskPanel>();
        surface = new QWidget(this);
        scrollbar = new QScrollBar(this);
        
        scrollbar.valueChanged.connect(this, "scrollbarChanged(int)");
        
        resizeEvent(width(), height());
        
        this.setFrameShadow(Shadow.Raised);
        this.setFrameShape(Shape.WinPanel);
        
        //surface.setStyleSheet("background-color: #ff9999;");
    }
    
    public void scrollbarChanged(int value) {
        int     y;
        
        y = -value;
        for (QWidget widget : widgets) {
            widget.move(0, y);
            y += widget.height();
        }
    }
    
    public void addWidget(QTaskPanel widget) {
        widget.setParent(this);
        widgets.add(widget);
        widget.resize(width() - scrollbar.width(), widget.height());
        widget.show();
        scrollbarChanged(scrollbar.value());
    }
    
    public void remWidget(QTaskPanel widget) {
        widgets.remove(widget);
        widget.hide();
        widget.setParent(null);
        scrollbarChanged(scrollbar.value());
    }
    
    private void resizeEvent(int w, int h) {
        int     th;
        
        surface.move(0, 0);
        surface.resize(w - 20, h);
        scrollbar.move(w - 20, 0);
        scrollbar.resize(20, h);
        
        th = 0;
        for (QWidget widget : widgets) {
            widget.resize(w - scrollbar.width(), widget.height());
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
