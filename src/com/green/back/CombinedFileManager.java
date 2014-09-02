package com.green.back;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public class CombinedFileManager implements Runnable {
	private LocalFileManager localFileManager;
	private RemoteFileManager remoteFileManager;
	private DatabaseConnection databaseConnection;
	
	public CombinedFileManager() {
		databaseConnection = new DatabaseConnection();
		localFileManager = new LocalFileManager("/Users/wsgreen/");
		Thread t = new Thread(localFileManager, "localFileManager");
		t.start();
	}

	@Override
	public void run() {
		WatchEvent<Path> ev = null;
		for(;;) {
			try {
				 ev = localFileManager.takeWatchEvent();
				 handleLocalEvent(ev);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	private void handleLocalEvent(WatchEvent<Path> ev) {
		if( ev.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
			
			databaseConnection.saveFile(ev.context().toString());
			System.out.println("Created file: " + ev.context());
		} else if (ev.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
			System.out.println("Deleted file: " + ev.context());
		} else if (ev.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
			System.out.println("Changed file: " + ev.context());
		}
	}
	
}
