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
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.DatatypeConverter;

import com.dropbox.core.DbxDelta.Entry;
import com.dropbox.core.DbxEntry;

public class CombinedFileManager implements Runnable {
	private LocalFileManager localFileManager;
	private RemoteFileManager remoteFileManager;
	private DatabaseConnection databaseConnection;
	
	private static ConcurrentHashMap<String, Integer> eventLog = new ConcurrentHashMap<String, Integer>();
	
	public CombinedFileManager() {
		databaseConnection = new DatabaseConnection();
		localFileManager = new LocalFileManager("/Users/wsgreen/InfiniteDrop");
		remoteFileManager = new RemoteFileManager();
		Thread localManager = new Thread(localFileManager, "localFileManager");
		Thread remoteManager = new Thread(remoteFileManager, "remoteFileManager");
		remoteManager.start();
		localManager.start();
	}

	@Override
	public void run() {
		Thread localWatch = new Thread() {
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
		};
		
		Thread remoteWatch = new Thread() {
			public void run() {
				Entry<DbxEntry> ev = null;
				for(;;) {
					try {
						 ev = remoteFileManager.takeDbxEvent();
						 handleRemoteEvent(ev);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		};
		
		localWatch.start();
		remoteWatch.start();

	}
	
	private void handleLocalEvent(SimpleEntry<Path, WatchEvent<Path>> e) {
		WatchEvent<Path> ev = e.getValue();
		String hash = getHash(localFileManager.getRelativePath(e.getKey().toString()));
		if(checkEventRepeat(hash)) {
			return;
		}
		
		if( ev.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
			String accnt = remoteFileManager.getLargestAccount();
			String path = localFileManager.getRelativePath(e.getKey().toString());
			String parent = localFileManager.getRelativePath(e.getKey().getParent().toString());
			if( e.getKey().toFile().isDirectory()) {
				remoteFileManager.saveFolder(e.getKey());
			} else {
				remoteFileManager.saveFile(e.getKey());
			}
			databaseConnection.saveFile(path, getHash(parent), accnt);
			System.out.println("Created file: " + ev.context());
		} else if (ev.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
			String client = databaseConnection.deleteFile(hash);
			remoteFileManager.deleteFile(hash, client);
			System.out.println("Deleted file: " + ev.context());
		} else if (ev.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
			System.out.println("Changed file: " + ev.context());
		}
	}
	
	private void handleRemoteEvent(Entry<DbxEntry> entry) {
		System.out.println("Received Dbx Event: " + entry.lcPath);
		String hash = entry.lcPath.substring(1).toUpperCase();
		if( entry.metadata == null ) {
			incrementLog(hash);
			localFileManager.deleteFile(databaseConnection.getFileName(hash));
			System.out.println("Deleted Local File");
		}
	}
	
	private void incrementLog(String hash) {
		if(CombinedFileManager.eventLog.containsKey(hash)) {
			int val = CombinedFileManager.eventLog.get(hash) + 1;
			CombinedFileManager.eventLog.put(hash, val);
		} else {
			CombinedFileManager.eventLog.put(hash, 1);
		}
	}
	
	private boolean checkEventRepeat(String hash) {
		if(CombinedFileManager.eventLog.containsKey(hash)) {
			int val = CombinedFileManager.eventLog.get(hash) - 1;
			if(val == 0){
				CombinedFileManager.eventLog.remove(hash);
			}else {
				CombinedFileManager.eventLog.put(hash, val);
			}
			
			return true;
		}else{
			return false;
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
