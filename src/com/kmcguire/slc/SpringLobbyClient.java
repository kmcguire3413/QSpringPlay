package com.kmcguire.slc;

import com.trolltech.qt.gui.QApplication;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SpringLobbyClient {
    public static byte[] loadResource(String path) throws IOException {
        InputStream             ins;
        byte[]                  sbuf;
        ByteArrayOutputStream   buf;
        int                     count;
        
        ins = SpringLobbyClient.class.getClassLoader().getResourceAsStream(path);
        
        buf = new ByteArrayOutputStream();
        sbuf = new byte[1024];
        
        while ((count = ins.read(sbuf)) > 0) {
            buf.write(sbuf, 0, count);
        }
        
        ins.close();
        
        return buf.toByteArray();
    }
    
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
