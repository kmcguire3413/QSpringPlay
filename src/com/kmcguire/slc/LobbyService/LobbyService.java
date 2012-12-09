package com.kmcguire.slc.LobbyService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;

public class LobbyService {
    private String              host;
    private short               port;
    private Socket              socket;
    private ByteBuffer          buf;
    private boolean             hasAuthenticated;
    private Set<Object>         eventHandlers;
    
    public LobbyService() {
        host = "lobby.springrts.com";
        port = 8200;
        hasAuthenticated = false;
        eventHandlers = new HashSet();
    }
    
    public LobbyService(String _host, String _user, String _pass) {
        if (_host.indexOf(':') > -1) {
            host = _host.substring(0, _host.indexOf(':'));
            port = Short.parseShort(_host.substring(_host.indexOf(':') + 1));
        } else {
            host = _host;
            port = 8200;
        }
        hasAuthenticated = false;
    }
    
    public void registerForEvents(Object o) {
        eventHandlers.add(o);
    }
    
    public void debug(String fmt, Object ... args) {
        System.out.printf(fmt, args);
    }
    
    /**
     * This will pass the event to all event handler classes that
     * implement a method using the annotation EventHandler and
     * also have this event as their one and only argument
     * @param event             event to pass to the handlers
     */
    public void callEvent(Event event) {
        Class[]         args;
        
        for (Object o : eventHandlers) {
            for (Method m : o.getClass().getDeclaredMethods()) {
                if (m.isAnnotationPresent(EventHandler.class)) {
                    args = m.getParameterTypes();
                    if (args.length == 1) {
                        if (args[0] == event.getClass()) {
                            try {
                                m.invoke(o, event);
                            } catch (IllegalAccessException ex) {
                                ex.printStackTrace();
                            } catch (InvocationTargetException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * This will generate the password hash in the proper form
     * to use in the password field when logging into the server. 
     * If by chance the MD5 algorithm is not found then a blank
     * password will be returned to prevent the plain text password
     * from being used, and an exception will simply be printed out.
     * 
     * @param pass              Plain text password
     * @return                  Hashed and encoded password
     */
    private String getPasswordHash(String pass) {
        MessageDigest       md5;
        
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return pass;
        }
        
        return new String(Base64.encodeBase64(md5.digest(pass.getBytes())));
    }
    
    public void sayBattle(String message) {
        OutputStream        ostrm;
        
        try {
            ostrm = socket.getOutputStream();
            ostrm.write(String.format("SAYBATTLE %s\n", message).getBytes());
        } catch (IOException ex) {
            callEvent(new NetworkErrorEvent(String.format("A IO exception occured creating the socket and connecting it.")));
            return;
        }
    }
    
    public void joinBattle(int id) {
        OutputStream        ostrm;
        
        try {
            ostrm = socket.getOutputStream();
            ostrm.write(String.format("JOINBATTLE %d\n", id).getBytes());
        } catch (IOException ex) {
            callEvent(new NetworkErrorEvent(String.format("A IO exception occured creating the socket and connecting it.")));
            return;
        }                        
    }
    
    public void sayChannel(String channel, String message) {
        OutputStream        ostrm;
        
        try {
            ostrm = socket.getOutputStream();
            ostrm.write(String.format("SAY %s %s\n", channel, message).getBytes());
        } catch (IOException ex) {
            callEvent(new NetworkErrorEvent(String.format("A IO exception occured creating the socket and connecting it.")));
            return;
        }                
    }
    
    public void joinChannel(String channel) {
        OutputStream        ostrm;
        
        try {
            ostrm = socket.getOutputStream();
            ostrm.write(String.format("JOIN %s\n", channel).getBytes());
        } catch (IOException ex) {
            callEvent(new NetworkErrorEvent(String.format("A IO exception occured creating the socket and connecting it.")));
            return;
        }        
    }
    
    private void processLine(byte[] bline) throws IOException {
        String              line;
        OutputStream        ostrm;
        
        line = new String(bline);
        
        line = line.trim();
        
        if (!hasAuthenticated) {
            AuthenticationEvent         event;
            String                      localIp;
            
            
            event = new AuthenticationEvent(line);
            callEvent(event);
            
            localIp = socket.getLocalAddress().getHostAddress();
            
            ostrm = socket.getOutputStream();
            
            // LOGIN user passhash 2194 localip clientversion
            ostrm.write(String.format("LOGIN %s %s 2194 %s %s\n", 
                    event.getUser(), getPasswordHash(event.getPass()),
                    localIp, event.getClientVersion()
            ).getBytes());
            ostrm.flush();
            hasAuthenticated = true;
            return;
        }
        
        NetMessage      nm;
        String          cmd;
        Event           event;
        
        nm = new NetMessage(line);
        
        cmd = nm.getWordParam();
       
        event = null;
        
        if (cmd.equals("BATTLEOPENED")) {
            event = new BattleOpenedEvent(
                    nm.getIntParam(), nm.getIntParam(), nm.getIntParam(),
                    nm.getWordParam(), nm.getWordParam(), nm.getIntParam(), nm.getIntParam(),
                    nm.getBoolParam(), nm.getIntParam(), nm.getLongParam(),
                    nm.getWordParam(), nm.getWordParam(), nm.getWordParam()
            );
        } else if (cmd.equals("JOINEDBATTLE")) {
            event = new JoinedBattleEvent(
                    nm.getIntParam(), nm.getWordParam(), nm.getWordParam()
            );
        } else if (cmd.equals("UPDATEBATTLEINFO")) {
            event = new UpdateBattleInfoEvent(
                    nm.getIntParam(), nm.getIntParam(), nm.getBoolParam(),
                    nm.getLongParam(), nm.getWordParam()
            );
        } else if (cmd.equals("MOTD")) {
            event = new MotdEvent( nm.getSentenceParam() );
        } else if (cmd.equals("ADDUSER")) {
            event = new AddUserEvent(nm.getWordParam(), nm.getWordParam(), nm.getIntParam());
        } else if (cmd.equals("ACCEPTED")) {
            event = new AcceptedEvent(nm.getWordParam());
        } else if (cmd.equals("CLIENTSTATUS")) {
            event = new ClientStatusEvent(nm.getWordParam(), nm.getIntParam());
        } else if (cmd.equals("LOGININFOEND")) {
            event = new LoginInfoEndEvent();
        } else if (cmd.equals("LEFTBATTLE")) {
            event = new LeftBattleEvent(nm.getIntParam(), nm.getWordParam());
        } else if (cmd.equals("JOIN")) {
            event = new JoinEvent(nm.getWordParam());
        } else if (cmd.equals("CLIENTS")) {
            event = new ClientsEvent(nm.getWordParam(), nm.getWordListParam());
        } else if (cmd.equals("CHANNELTOPIC")) {
            event = new ChannelTopicEvent(
                    nm.getWordParam(), nm.getWordParam(), nm.getIntParam(),
                    nm.getRemaining()
            );
        } else if (cmd.equals("REMOVEUSER")) {
            event = new RemoveUserEvent(nm.getWordParam());
        } else if (cmd.equals("BATTLECLOSED")) {
            event = new BattleClosedEvent(nm.getIntParam());
        } else if (cmd.equals("JOINED")) {
            event = new JoinedEvent(nm.getWordParam(), nm.getWordParam());
        } else if (cmd.equals("JOINFAILED")) {
            event = new JoinFailedEvent(nm.getWordParam(), nm.getRemaining());
        } else if (cmd.equals("JOINBATTLE")) {
            event = new JoinBattleEvent(nm.getIntParam(), nm.getLongParam());
        } else if (cmd.equals("SETSCRIPTTAGS")) {
            String[]                tags;
            Map<String, String>     map;
            
            tags = nm.getSentenceListParam();
            map = new HashMap<String, String>();
            
            for (String tag : tags) {
                map.put(tag.substring(0, tag.indexOf('=')), tag.substring(tag.indexOf('=') + 1));
            }
            
            event = new SetScriptTagsEvent(map);
        } else if (cmd.equals("CLIENTBATTLESTATUS")) {
            event = new ClientBattleStatusEvent(
                    nm.getWordParam(), nm.getIntParam(), 
                    nm.getIntParam()
            );
        } else if (cmd.equals("ADDSTARTRECT")) {
            event = new AddStartRectEvent(
                    nm.getIntParam(), nm.getIntParam(), nm.getIntParam(),
                    nm.getIntParam(), nm.getIntParam()
            );
        } else if (cmd.equals("REQUESTBATTLESTATUS")) {
            event = new RequestBattleStatusEvent();
        } else if (cmd.equals("JOINBATTLEFAILED")) {
            event = new JoinBattleFailedEvent(nm.getRemaining());
        } else if (cmd.equals("SAIDBATTLEEX")) {
            event = new SaidBattleExEvent(nm.getWordParam(), nm.getRemaining());
        } else if (cmd.equals("SAIDBATTLE")) {
            event = new SaidBattleEvent(nm.getWordParam(), nm.getRemaining());
        } else if (cmd.equals("DENIED")) {
            event = new DeniedEvent(nm.getRemaining());
        } else if (cmd.equals("LEFT")) {
            event = new LeftEvent(nm.getWordParam(), nm.getWordParam(), nm.getRemaining());
        }
        
        if (event != null) {
            callEvent(event);
        } else {
            System.out.printf("unhandled:[%s]\n", line);
        }
    }
    
    private void lineReader(ByteBuffer buf) throws IOException {
            int         x;
            byte[]      lbuf;
        
            buf.flip();
            
            do {
                for (x = 0; x < buf.limit(); ++x) {
                    if (buf.get(x) == 10) {
                        lbuf = new byte[x];
                        buf.get(lbuf, 0, x);
                        buf.get();
                        buf.compact();
                        buf.flip();
                        processLine(lbuf);
                        break;
                    }
                }
            } while (x < buf.limit());
            
            buf.position(buf.limit());
            buf.limit(buf.capacity());
    }
    
    public void tick() {
        InputStream     istrm;
        
        // are we connected? if not try to connect
        if (socket == null) {
            socket = new Socket();
        }
        
        if (!socket.isConnected()) {
            InetAddress         ina;
            InetSocketAddress   sa;
            
            buf = ByteBuffer.allocate(4096 * 4);
            
            try {
                ina = Inet4Address.getByName(host);
            } catch (UnknownHostException ex) {
                callEvent(new NetworkErrorEvent(String.format("The host '%s' can not be resolved.", host)));
                return;
            }
            
            sa = new InetSocketAddress(ina, port);
            
            try {
                socket.connect(sa);
            } catch (IOException ex) {
                callEvent(new NetworkErrorEvent(String.format("A IO exception occured creating the socket and connecting it.")));
                return;
            }
            
            return;
        }
        
        try {
            istrm = socket.getInputStream();
        
            byte[]          lbuf;
            byte[]          tbuf;
            String          line;
            int             x;
            int             a;
            
            a = buf.remaining() < istrm.available() ? buf.remaining() : istrm.available();
            
            if (a > 0) {

                lbuf = new byte[a];

                istrm.read(lbuf);            
                buf.put(lbuf);   

                lineReader(buf);
            }
        } catch (IOException ex) {
            callEvent(new NetworkErrorEvent(String.format("A IO exception occured creating the socket and connecting it.")));
            return;
        }
        
        return;
    }
}

