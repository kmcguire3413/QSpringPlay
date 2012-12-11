package com.kmcguire.slc;

import com.trolltech.qt.gui.QImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

class Pair {
    private String          mapName;
    private MapManagerCb    runnable;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public MapManagerCb getRunnable() {
        return runnable;
    }

    public void setRunnable(MapManagerCb runnable) {
        this.runnable = runnable;
    }

    public Pair(String mapName, MapManagerCb runnable) {
        this.mapName = mapName;
        this.runnable = runnable;
    }
}

public class MapManager implements Runnable {
    private static MapManager                                            instance;
    private static Thread                                                thread;
    private static final Map<String, QImage>                             cache;
    private static final ConcurrentLinkedDeque<Pair>  requests;
    
    static {
        instance = null;
        cache = new ConcurrentHashMap<String, QImage>();
        requests = new ConcurrentLinkedDeque<Pair>();
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
    
    public QImage requestMinimap(String mapName, MapManagerCb runnable) {
        mapName = mapName.replace(' ', '_');
        if (cache.get(mapName) != null) {
            return cache.get(mapName);
        }
        
        requests.add(new Pair(mapName, runnable));
        
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
        String          mapName;
        QImage          img;
        Pair            pair;
        MapManagerCb    runnable;
        
        while (true) {
            synchronized (requests) {
                try {
                    requests.wait(5000);
                } catch (InterruptedException ex) {
                }
            }
            
            for (int i = 0; i < requests.size(); ++i) {
                pair = requests.poll();
                runnable = pair.getRunnable();
                mapName = pair.getMapName();
                if (cache.get(mapName) != null) {
                    // we already downloaded it, but go ahead
                    // and call the callback just to be sure
                    runnable.run(mapName, cache.get(mapName));
                    continue;
                } else {
                    img = fetchMinimap(mapName);
                    if (img == null) {
                        requests.add(pair);
                        continue;
                    }
                }
                // ... if we are here then we have fetched the minimap
                // so now we need to put it in the cache and call any
                // callback that needs to be called
                cache.put(mapName, img);
                runnable.run(mapName, img);
                //
            }
        }
    }
}
