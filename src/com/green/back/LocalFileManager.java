package com.green.back;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.StandardWatchEventKinds;
import java.util.concurrent.ArrayBlockingQueue;

public class LocalFileManager implements Runnable {
	private boolean running = false;
	private WatchService watcher;
	private Path dir;
	private ArrayBlockingQueue<WatchEvent<Path>> eventQueue;
	
	public LocalFileManager(String path) {
		eventQueue = new ArrayBlockingQueue<WatchEvent<Path>>(200);
		dir = Paths.get(path);
		try {
			watcher = FileSystems.getDefault().newWatchService();
			WatchKey key = dir.register(watcher,
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public WatchEvent<Path> takeWatchEvent() throws InterruptedException {
		return eventQueue.take();
	}
	
	public synchronized void onEvent(WatchEvent<Path> ev) {
		if( ev.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
			System.out.println("Created file: " + ev.context());
		} else if (ev.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
			System.out.println("Deleted file: " + ev.context());
		} else if (ev.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
			System.out.println("Changed file: " + ev.context());
		}
	}
	
	public void updateLocalPath(String path, String file) {
		
	}

	@Override
	public void run() {
		for(;;) {

		    // wait for key to be signaled
		    WatchKey key;
		    try {
		        key = watcher.take();
		    } catch (InterruptedException x) {
		        return;
		    }

		    for (WatchEvent<?> event: key.pollEvents()) {

		        // This key is registered only
		        // for ENTRY_CREATE events,
		        // but an OVERFLOW event can
		        // occur regardless if events
		        // are lost or discarded.
		        if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
		            continue;
		        }
		        
		        if( event.context().toString().endsWith(".swp")) {
		        	continue;
		        }

		        // The filename is the
		        // context of the event.
		        WatchEvent<Path> ev = (WatchEvent<Path>)event;
		        Path filename = ev.context();

		        // Email the file to the
		        //  specified email alias.
		        //onEvent(ev);
		        eventQueue.add(ev);
		    }

		    // Reset the key -- this step is critical if you want to
		    // receive further watch events.  If the key is no longer valid,
		    // the directory is inaccessible so exit the loop.
		    boolean valid = key.reset();
		    if (!valid) {
		        break;
		    }
		}
		
	}

}
