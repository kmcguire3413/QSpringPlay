package com.kmcguire.slc;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QStyle;
import com.trolltech.qt.gui.QStyleFactory;

public class SpringLobbyClient {

    public static void main(String[] args) {
        QApplication            app;
        MainWindow              win;
        //QStyle                  style;
        
        app = new QApplication(args);
        
        //style = QStyleFactory.create("Plastique");
        
        win = new MainWindow();
        win.resize(600, 480);
        win.move(100, 100);
        win.setWindowTitle("SpringLobbyClient");
        win.show();
        
        QApplication.exec();
    }
}
