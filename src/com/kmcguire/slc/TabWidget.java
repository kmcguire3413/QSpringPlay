package com.kmcguire.slc;

import com.trolltech.qt.gui.QFrame;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QTabBar;
import com.trolltech.qt.gui.QWidget;
import java.util.HashMap;
import java.util.Map;

/**
 * The only reason this class exists is because the original implementation
 * of QTabWidget was broken. So this is a work around to make something like
 * it that works. If it is ever fixed it can replace this class.
 * @author kmcguire
 */
public class TabWidget extends QWidget {
    private QTabBar                 tabBar;
    private QFrame                  surface;
    private Map<Integer, QWidget>   map;
    private int                     curIndex;
    
    public TabWidget() {
        super();
        
        tabBar = new QTabBar(this);
        surface = new QFrame(this);
       
        map = new HashMap<Integer, QWidget>();
        
        tabBar.show();
        surface.show();
        
        tabBar.currentChanged.connect(this, "tabChanged(int)");
        
        surface.setStyleSheet("background-color: green;");
        
        curIndex = -1;
    }
    
    public void tabChanged(int i) {
        System.out.printf("i:%d\n", i);
        if (curIndex > -1) {
            map.get(curIndex).hide();
        }
        curIndex = i;
        map.get(i).show();
        System.out.printf("visible:%b\n", map.get(i).isVisible());
        map.get(i).resize(surface.width(), surface.height());
    }
    
    @Override
    public void resizeEvent(QResizeEvent event) {
        resizeEvent(width(), height());
    }
    
    public void resizeEvent(int w, int h) {
        tabBar.resize(w, 20);
        System.out.printf("resizeEvent!! %d\n", tabBar.height());
        surface.move(0, tabBar.height());
        surface.resize(w, height() - tabBar.height());
        if (curIndex > -1) {
            System.out.printf("curIndex:%d\n", curIndex);
            map.get(curIndex).resize(surface.width(), surface.height());
        }
    }
    
    public void addTab(QWidget widget, String title) {
        int             i;
        // work around because addTab calls some methods above
        // and we need to have it added to the map but we can
        // not because we need the index, well in this case we
        // know the first index since the first tab added is
        // automatically actived/selected unlike any others
        if (map.size() == 0) {
            map.put(0, widget);
        }
        widget.setParent(surface);
        i = tabBar.addTab(title);
        map.put(i, widget);
    }
}
