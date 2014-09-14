package com.green.back;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import almonds.FindCallback;
import almonds.ParseException;
import almonds.ParseObject;

import com.dropbox.core.*;

public class RemoteFileManager {
	private DatabaseConnection databaseConnection;
	private HashMap<String, DbxClient> clients;

	public RemoteFileManager() {		
		clients = new HashMap<String, DbxClient>();
		databaseConnection = new DatabaseConnection();
	}
	
	public void saveFile(Path path) {
		File inputFile = path.toFile();
		DbxClient client = databaseConnection.getDbxClient(getLargestAccount());
		String hash = CombinedFileManager.getHash(LocalFileManager.getRelativePath(path.toString()));
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(inputFile);
		    DbxEntry.File uploadedFile = client.uploadFile("/" + hash,
		        DbxWriteMode.force(), inputFile.length(), inputStream);
		    System.out.println("Uploaded: " + uploadedFile.toString());
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveFolder(Path path) {
		DbxClient client = databaseConnection.getDbxClient(getLargestAccount());
		String hash = CombinedFileManager.getHash(LocalFileManager.getRelativePath(path.toString()));
		try {
			client.createFolder("/" + hash);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteFile(String path, String clientToken) {
		DbxClient client = databaseConnection.getDbxClient(clientToken);
		try {
			client.delete("/" + path);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getLargestAccount() {
		long max = Long.MIN_VALUE;
		String dbxAccnt = null;
		for(DbxClient client: databaseConnection.getDbxClients()) {
			long curr = Long.MIN_VALUE;
			try {
				curr = client.getAccountInfo().quota.total - client.getAccountInfo().quota.normal;
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(curr > max) {
				max = curr;
				dbxAccnt = client.getAccessToken();
			}
		}
		return dbxAccnt;
	}
}
