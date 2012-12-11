package com.kmcguire.slc;

import com.trolltech.qt.gui.QImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MapManager implements Runnable {
    private static MapManager                           instance;
    private static Thread                               thread;
    private static final Map<String, QImage>            cache;
    private static final ConcurrentLinkedDeque<String>  requests;
    
    static {
        instance = null;
        cache = new ConcurrentHashMap<String, QImage>();
        requests = new ConcurrentLinkedDeque<String>();
    }
    
    private MapManager() {
        
    }
    
    public static MapManager getInstance() {
        if (instance == null) {            
            instance = new MapManager();
            thread = new Thread(instance);
            thread.start();
        }
        
        
        return instance;
    }
    
    public QImage requestMinimap(String mapName) {
        mapName = mapName.replace(' ', '_');
        if (cache.get(mapName) != null) {
            return cache.get(mapName);
        }
        
        requests.add(mapName);
        
        synchronized (requests) {
            requests.notify();
        }
        return null;
    }
    
    public QImage fetchMinimap(String mapName) {
        String                  url;
        URLConnection           connection;
        InputStream             response;
        ByteArrayOutputStream   bb;
        byte[]                  bbuf;
        int                     ava;
        QImage                  img;
                
        url = String.format("http://zero-k.info/Resources/%s.minimap.jpg", mapName);
        
        try {
            connection = new URL(url).openConnection();
            connection.setDoOutput(false); // GET
            response = connection.getInputStream();
            
            bb = new ByteArrayOutputStream();
            bbuf = new byte[1024];

            while (response.available() > 0) {
                ava = response.available();
                if (ava > bbuf.length) {
                    ava = bbuf.length;
                }
                response.read(bbuf);
                
                bb.write(bbuf, 0, ava);
            }
            
            img = new QImage();
            img.loadFromData(bb.toByteArray());
            return img;
        } catch (MalformedURLException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }
    
    @Override
    public void run() {
        String      mapName;
        QImage      img;
        
        while (true) {
            synchronized (requests) {
                try {
                    requests.wait(5000);
                } catch (InterruptedException ex) {
                }
            }
            
            for (int i = 0; i < requests.size(); ++i) {
                mapName = requests.poll();
                if (cache.get(mapName) != null) {
                    // forgot about it we already downloaded
                    // it and called any callback handler
                    continue;
                } else {
                    img = fetchMinimap(mapName);
                    if (img == null) {
                        requests.add(mapName);
                        continue;
                    }
                }
                // ... if we are here then we have fetched the minimap
                // so now we need to put it in the cache and call any
                // callback that needs to be called
                cache.put(mapName, img);
                //
            }
        }
    }
}
