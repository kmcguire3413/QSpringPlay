package com.kmcguire.slc;

import com.kmcguire.slc.LobbyService.AuthenticationEvent;
import com.kmcguire.slc.LobbyService.EventHandler;
import com.trolltech.qt.core.Qt.CheckState;
import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QWidget;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LoginPanel extends Panel {
    private QGridLayout                 grid;
    private QLabel                      labelUsername;
    private QLineEdit                   editUsername;
    private QLabel                      labelPassword;
    private QLineEdit                   editPassword;
    private QPushButton                 btnLogin;    
    private QWidget                     smallBox;
    private QCheckBox                   chkRemember;
    private File                        pwfile;
    private MainWindow                  mwin;
    
    public LoginPanel(MainWindow _mwin) {
        
        mwin = _mwin;
        
        pwfile = new File("logininfo");
        
        smallBox = new QWidget(this);
        
        grid = new QGridLayout();
        
        labelUsername = new QLabel();
        editUsername = new QLineEdit();
        labelPassword = new QLabel();
        editPassword = new QLineEdit();
        btnLogin = new QPushButton();
        chkRemember = new QCheckBox();
        
        labelUsername.setText("Username:");
        labelPassword.setText("Password:");
        chkRemember.setText("Remember Password?");
        btnLogin.setText("Login");
        
        btnLogin.clicked.connect(this, "btnLoginClicked(boolean)");
        
        if (pwfile.exists()) {
            RandomAccessFile        raf;
            
            try {
                byte[]          buf;
                String          part;
                
                raf = new RandomAccessFile(pwfile, "rw");
                buf = new byte[(int)raf.length()];
                raf.read(buf);
                raf.close();
                
                part = new String(buf);
                
                editUsername.setText(part.substring(0, part.indexOf(':')));
                editPassword.setText(part.substring(part.indexOf(':') + 1));
                
                chkRemember.setCheckState(CheckState.Checked);
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            }
        }
        
        grid.addWidget(labelUsername, 0, 0);
        grid.addWidget(editUsername, 0, 1);
        grid.addWidget(labelPassword, 1, 0);
        grid.addWidget(editPassword, 1, 1);
        grid.addWidget(btnLogin, 2, 0);
        grid.addWidget(chkRemember, 2, 1);
        
        smallBox.setLayout(grid);
        
        mwin.getLobbyService().registerForEvents(this);
    }

    @EventHandler
    public void onLobbyAuthentication(AuthenticationEvent event) {
        event.setUser("kmcguire");
        event.setPass("tty5413413");
        event.setClientVersion("ZK 2.75.0.4.1273148089.a speb");
    }
    
    public void btnLoginClicked(boolean checked) {
        RandomAccessFile        raf;
        
        if (chkRemember.checkState() == CheckState.Checked) {
            try {
                raf = new RandomAccessFile(pwfile, "rw");
                raf.write(String.format("%s:%s", editUsername.text(), editPassword.text()).getBytes());
                raf.close();
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            }
        } else {
            if (pwfile.exists()) {
                pwfile.delete();
            }
        }
        
        mwin.getLobbyService().setDoConnect(true);
    }
    
    @Override
    public void resizeEvent(QResizeEvent event) {
        smallBox.resize(250, 100);
        smallBox.move((int)(width() * 0.5 - smallBox.width() * 0.5), (int)(height() * 0.5 - smallBox.height() * 0.5));
    }
    
    @Override
    public String getTitle() {
        return "Main";
    }
}
