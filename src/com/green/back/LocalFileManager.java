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
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ArrayBlockingQueue;

public class LocalFileManager implements Runnable {
	private final static String BASE_DIR = System.getProperty("user.home") + "/InfiniteDrop/";
	private WatchService watcher;
	private WatchDir watchDir;
	private Path dir;
	private static ArrayBlockingQueue<SimpleEntry<Path, WatchEvent<Path>>> eventQueue;
	
	public LocalFileManager(String path) {
		LocalFileManager.eventQueue = new ArrayBlockingQueue<SimpleEntry<Path, WatchEvent<Path>>>(200);
		dir = Paths.get(path);
		try {
			watchDir = new WatchDir(dir, true, eventQueue);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
	
	@Override
	public void run() {
		watchDir.processEvents();
		
	}
	public SimpleEntry<Path, WatchEvent<Path>> takeWatchEvent() throws InterruptedException {
		return LocalFileManager.eventQueue.take();
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
	
	public static String getRelativePath(String path) {
		if(path.length() > BASE_DIR.length())
			return path.substring(BASE_DIR.length());
		else
			return "";
	}
	
	public void deleteFile(String path) {
		try {
			Files.delete(Paths.get(BASE_DIR + path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
