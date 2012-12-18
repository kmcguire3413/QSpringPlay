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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
    private boolean             doConnect;
    //
    private long                bytesIn;
    private long                bytesOut;
    private long                eventsIn;
    private long                eventsUnhandled;
    private long                linesIn;
    private long                lineBufSize;

    public long getBytesIn() {
        return bytesIn;
    }

    public long getBytesOut() {
        return bytesOut;
    }

    public long getEventsIn() {
        return eventsIn;
    }

    public long getEventsUnhandled() {
        return eventsUnhandled;
    }

    public long getLineBufSize() {
        return lineBufSize;
    }

    public long getLinesIn() {
        return linesIn;
    }
    
    private void resetStats() {
        bytesIn = 0;
        bytesOut = 0;
        eventsIn = 0;
        linesIn = 0;
        lineBufSize = 0;
        eventsUnhandled = 0;
    }
    
    private void write(OutputStream ostrm, byte[] data) throws IOException {
        bytesOut += data.length;
        ostrm.write(data);
    }
    
    public boolean isDoConnect() {
        return doConnect;
    }
    
    public void login() {
        doConnect = true;
    }
    
    public LobbyService() {
        host = "lobby.springrts.com";
        port = 8200;
        eventHandlers = new HashSet();
        doConnect = false;
    }
    
    public LobbyService(String _host, String _user, String _pass) {
        if (_host.indexOf(':') > -1) {
            host = _host.substring(0, _host.indexOf(':'));
            port = Short.parseShort(_host.substring(_host.indexOf(':') + 1));
        } else {
            host = _host;
            port = 8200;
        }
        eventHandlers = new HashSet();
        doConnect = false;
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
        class Pair {
            Method          m;
            Object          o;
            int             p;
        }
        
        Class[]         args;
        Object[]        _eventHandlers;
        Set<Pair>       calls;
        Pair            pair;
        EventPriority   ep;
        Iterator<Pair>  i;
        Pair            hp;
        int             hv;
        
        calls = new HashSet<Pair>();
        
        _eventHandlers = eventHandlers.toArray();
        
        for (Object o : _eventHandlers) {
            for (Method m : o.getClass().getDeclaredMethods()) {
                if (m.isAnnotationPresent(EventHandler.class)) {
                    args = m.getParameterTypes();
                    if (args.length == 1) {
                        if (args[0] == event.getClass()) {
                            pair = new Pair();
                            ep = m.getAnnotation(EventHandler.class).priority();
                            pair.p = ep.getSlot();
                            pair.m = m;
                            pair.o = o;
                            calls.add(pair);
                        }
                    }
                }
            }
        }
        
        while (calls.size() > 0) {
            hv = -1;
            hp = null;
            i = calls.iterator();
            for (Pair _pair : calls) {
                _pair = i.next();
                if (_pair.p > hv) {
                    hv = _pair.p;
                    hp = _pair;
                }
            }
            calls.remove(hp);
            try {
                hp.m.setAccessible(true);
                hp.m.invoke(hp.o, event);
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
            //
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
            write(ostrm, String.format("SAYBATTLE %s\n", message).getBytes());
        } catch (IOException ex) {
            callEvent(new NetworkErrorEvent(String.format("A IO exception occured creating the socket and connecting it.")));
            return;
        }
    }
    
    public void sendBattleStatus(int status, int color) {
        OutputStream        ostrm;
        
        try {
            ostrm = socket.getOutputStream();
            write(ostrm, String.format("MYBATTLESTATUS %d %d\n", status, color).getBytes());
        } catch (IOException ex) {
            callEvent(new NetworkErrorEvent(String.format("A IO exception occured creating the socket and connecting it.")));
            return;
        }        
    }
    
    public void joinBattle(int id) {
        OutputStream        ostrm;
        
        try {
            ostrm = socket.getOutputStream();
            write(ostrm, String.format("JOINBATTLE %d * 1904189322\n", id).getBytes());
        } catch (IOException ex) {
            callEvent(new NetworkErrorEvent(String.format("A IO exception occured creating the socket and connecting it.")));
            return;
        }                        
    }
    
    public void sayChannel(String channel, String message) {
        OutputStream        ostrm;
        
        try {
            ostrm = socket.getOutputStream();
            write(ostrm, String.format("SAY %s %s\n", channel, message).getBytes());
        } catch (IOException ex) {
            callEvent(new NetworkErrorEvent(String.format("A IO exception occured creating the socket and connecting it.")));
            return;
        }                
    }

    public void logout() {
        OutputStream        ostrm;
        
        try {
            ostrm = socket.getOutputStream();
            write(ostrm, "QUIT\n".getBytes());
            ostrm.flush();
            socket.close();
            callEvent(new LogoutEvent());
            doConnect = false;
            /*
             * I reset stats here then also in the tick method when doConnect
             * is set to false like I am doing above, but the problem is that
             * the tick method is not synchrnous with this meaning we could
             * reconnect and therefore not reset the stats so this just ensures
             * that we reset them. Also the connection can be terminated (logout)
             * by manually setting doConnect to false and not calling this function
             * but that is not supported.
             */
            resetStats();
        } catch (IOException ex) {
            callEvent(new NetworkErrorEvent(String.format("A IO exception occured creating the socket and connecting it.")));
            return;
        }        
    }
    
    public void joinChannel(String channel) {
        OutputStream        ostrm;
        
        try {
            ostrm = socket.getOutputStream();
            write(ostrm, String.format("JOIN %s\n", channel).getBytes());
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
            write(ostrm, String.format("LOGIN %s %s 2194 %s %s\n", 
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
                    nm.getSentenceParam(), nm.getSentenceParam(), nm.getSentenceParam()
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
        } else if (cmd.equals("SAID")) {
            event = new SaidEvent(nm.getWordParam(), nm.getWordParam(), nm.getRemaining());
        }
        
        ++eventsIn;
        
        if (event != null) {
            //System.out.printf("event:%s\n", event.getClass().getName());
            callEvent(event);
        } else {
            ++eventsUnhandled;
            System.out.printf("unhandled:[%s]\n", line);
        }
    }
    
    /**
     * This breaks a byte buffer down into lines as they are placed
     * into the byte buffer, and it hands the broken lines to the
     * line processor.
     * @param buf               A byte buffer in which new data is appended onto.
     * @throws IOException      Thrown when in some cases an event passes data back
     *                          which is written onto the socket such as login data.
     */
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
                        ++linesIn;
                        processLine(lbuf);
                        break;
                    }
                }
            } while (x < buf.limit());
            
            buf.position(buf.limit());
            buf.limit(buf.capacity());
            
            lineBufSize = buf.position();
    }
    
    /**
     * This handles connecting the socket, checking if it has data, and
     * processing the data and handing out events as required. It only
     * blocks on socket connect, but I need to fix that so it does not
     * block.
     */
    public void tick() {
        InputStream     istrm;
        
        // are we connected? if not try to connect
        if (socket == null) {
            socket = new Socket();
        }
        
        // early exit if doConnect is false
        if (!doConnect) {
            if (socket.isConnected()) {
                try {
                    System.out.printf("closing connection\n");
                    socket.close();
                    socket = new Socket();
                } catch (IOException ex) {
                    callEvent(new NetworkErrorEvent(String.format("A IO exception occured closing the socket.")));    
                }
            }
            System.out.printf("idle\n");
            return;
        }
        
        // of course doConnect will be true
        if (!socket.isConnected()) {
            InetAddress         ina;
            InetSocketAddress   sa;
            
            System.out.printf("trying to connect!\n");
            
            resetStats();
            
            hasAuthenticated = false;
            
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

                bytesIn += a;
                
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

