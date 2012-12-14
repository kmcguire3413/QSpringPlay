package com.kmcguire.slc;

import com.trolltech.qt.gui.QPixmap;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
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
    private static final Map<String, QPixmap>                            cache;
    private static final ConcurrentLinkedDeque<Pair>  requests;
    
    static {
        instance = null;
        cache = new ConcurrentHashMap<String, QPixmap>();
        requests = new ConcurrentLinkedDeque<Pair>();
    }
    
    private MapManager() {
        
    }
    
    public static MapManager getInstance() {
        if (instance == null) {            
            instance = new MapManager();
            thread = new Thread(instance);
            thread.setDaemon(true);
            thread.start();
        }
        
        
        return instance;
    }
    
    public QPixmap requestMinimap(String mapName, MapManagerCb runnable) {
        File                file;
        byte[]              bbuf;
        QPixmap             img;
        
        mapName = mapName.replace(' ', '_');
        if (cache.get(mapName) != null) {
            return cache.get(mapName);
        }
        
        file = new File(String.format("~/qsl/miniMapCache/%s.jpg", mapName));

        if (file.exists()) {
            RandomAccessFile            raf;
            
            try {
                raf = new RandomAccessFile(file, "rw");
                bbuf = new byte[(int)raf.length()];
                raf.read(bbuf);
                raf.close();
                img = new QPixmap();
                img.loadFromData(bbuf);
                System.out.printf("@@got image from cache for %s\n", mapName);
                return img;                
            } catch (FileNotFoundException ex) {
                System.out.printf("warning: could not access minimap in ~/qsl/miniMapCache/");
            } catch (IOException ex) {
                System.out.printf("warning: could not access minimap in ~/qsl/miniMapCache/");
            }
        }
        
        requests.add(new Pair(mapName, runnable));
        
        synchronized (requests) {
            requests.notify();
        }
        return null;
    }
    
    public QPixmap fetchMinimap(String mapName) {
        String                  url;
        URLConnection           connection;
        InputStream             response;
        ByteArrayOutputStream   bb;
        byte[]                  bbuf;
        int                     ava;
        QPixmap                 img;
        int                     cnt;
        File                    file;
        
        url = String.format("http://zero-k.info/Resources/%s.minimap.jpg", mapName);
        
        System.out.printf("fetching %s\n", mapName);
        
        try {
            connection = new URL(url).openConnection();
            connection.setDoOutput(false); // GET
            response = connection.getInputStream();
            
            bb = new ByteArrayOutputStream();
            bbuf = new byte[1024];

            while ((cnt = response.read(bbuf)) > -1) {
                bb.write(bbuf, 0, cnt);
            }
            
            System.out.printf("fetch success %s\n", mapName);
            
            file = new File("~/qsl/miniMapCache/");
            
            if (!file.exists()) {
                file.mkdirs();
            }

            RandomAccessFile            raf;
            
            file = new File(String.format("~/qsl/miniMapCache/%s.jpg", mapName));
            
            if (file.exists()) {
                file.delete();
            }
            
            bbuf = bb.toByteArray();
            
            raf = new RandomAccessFile(file, "rw");
            raf.write(bbuf);
            raf.close();
            
            img = new QPixmap();
            img.loadFromData(bbuf);
            return img;
        } catch (MalformedURLException ex) {
            System.out.printf("ex\n");
            return null;
        } catch (IOException ex) {
            System.out.printf("ex\n");
            return null;
        }
    }
    
    @Override
    public void run() {
        String          mapName;
        QPixmap         img;
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
