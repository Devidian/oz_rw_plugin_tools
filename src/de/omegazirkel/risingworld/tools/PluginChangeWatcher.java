/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.omegazirkel.risingworld.tools;

import java.io.File;
import java.io.IOException;
// import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Maik *Devidian* Laschober
 */
public class PluginChangeWatcher {

    private static WatchService watcher = null;
    private static final Map<WatchKey, Path> keyPaths = new ConcurrentHashMap<WatchKey, Path>();
    private static final Map<WatchKey, FileChangeListener> keyListener = new ConcurrentHashMap<WatchKey, FileChangeListener>();
    private static volatile Thread processingThread;
    
	public static OZLogger logger() {
        return OZLogger.getInstance("OZ.Tools.ChangeWatcher");
    }

    /**
     * 
     * @param flc
     * @param dir
     * @return
     */
    public static WatchKey registerFileChangeListener(final FileChangeListener flc, final File dir) {
        initListenerThread();
        // register
        try {
            final Path p = dir.toPath();
            logger().out("register " + p);
            final WatchKey key = p.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            keyPaths.put(key, p);
            keyListener.put(key, flc);
            return key;
        } catch (Exception e) {
            logger().out("registerFileChangeListener-> " + e.toString(), 911);
        }
        return null;
    }

    /**
     * 
     * @param key
     */
    public static void unregisterFileChangeListener(final WatchKey key) {
        keyPaths.remove(key);
        keyListener.remove(key);
    }

    /**
     * 
     */
    public static void initListenerThread() {
        if (processingThread == null) {
            processingThread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.currentThread().setName("ChangeWatcher");
                        processFileNotifications();
                    } catch (final InterruptedException ex) {
                        logger().out("processingThread-> " + ex.toString());
                        processingThread = null;
                    } catch (IOException e) {
                        logger().out("processingThread-> " + e.toString());
                        processingThread = null;
                    }
                }
            };

            processingThread.start();
        }
    }

    /**
     *
     */
    public static void shutDownListener() {
        final Thread thr = processingThread;
        if (thr != null) {
            thr.interrupt();
        }
    }

    /**
     *
     * @throws InterruptedException
     * @throws IOException
     */
    private static void processFileNotifications() throws InterruptedException, IOException {
        while (true) {
            if (watcher == null) {
                watcher = FileSystems.getDefault().newWatchService();
                logger().out("Start WatchUpdates");
            }
            final WatchKey key = watcher.take();
            if (keyPaths.containsKey(key) && keyListener.containsKey(key)) {

                final Path dir = (Path) keyPaths.get(key);
                final FileChangeListener fcl = (FileChangeListener) keyListener.get(key);
                key.pollEvents().forEach((evt) -> {
                    final WatchEvent.Kind<?> eventType = evt.kind();
                    if (!(eventType == OVERFLOW)) {
                        final Object o = evt.context();
                        if (o instanceof Path) {
                            final Path path = (Path) o;
                            process(dir, path, eventType, fcl);
                        }
                    }
                });
            }
            key.reset();

        }
    }

    /**
     * 
     * @param dir
     * @param file
     * @param evtType
     * @param fcl
     */
    private static void process(final Path dir, final Path file, final WatchEvent.Kind<?> evtType,
            final FileChangeListener fcl) {
        if (evtType == ENTRY_MODIFY) {
            fcl.onFileChangeEvent(file);
        }
        if (evtType == ENTRY_CREATE && !file.toString().startsWith(".")) {
            fcl.onFileCreateEvent(file);
        }
    }
}
