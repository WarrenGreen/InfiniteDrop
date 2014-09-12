package com.green.back;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap.SimpleEntry;

import javax.xml.bind.DatatypeConverter;

public class CombinedFileManager implements Runnable {
	private LocalFileManager localFileManager;
	private RemoteFileManager remoteFileManager;
	private DatabaseConnection databaseConnection;
	
	public CombinedFileManager() {
		databaseConnection = new DatabaseConnection();
		localFileManager = new LocalFileManager("/Users/wsgreen/InfiniteDrop");
		remoteFileManager = new RemoteFileManager();
		Thread t = new Thread(localFileManager, "localFileManager");
		t.start();
	}

	@Override
	public void run() {
		SimpleEntry<Path, WatchEvent<Path>> ev = null;
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
	
	private void handleLocalEvent(SimpleEntry<Path, WatchEvent<Path>> e) {
		WatchEvent<Path> ev = e.getValue();
		if( ev.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
			String accnt = remoteFileManager.getLargestAccount();
			String path = localFileManager.getRelativePath(e.getKey().toString());
			String parent = localFileManager.getRelativePath(e.getKey().getParent().toString());
			databaseConnection.saveFile(path, getHash(parent), accnt);
			System.out.println("Created file: " + ev.context());
		} else if (ev.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
			String hash = getHash(localFileManager.getRelativePath(e.getKey().toString()));
			databaseConnection.deleteFile(hash);
			System.out.println("Deleted file: " + ev.context());
		} else if (ev.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
			System.out.println("Changed file: " + ev.context());
		}
	}
	
	public static String getHash(String file) {
		String hash = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5",
					new sun.security.provider.Sun());
			md.update(file.getBytes("UTF-8"));
			hash = DatatypeConverter.printHexBinary(md.digest());

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hash;
	}
	
}
