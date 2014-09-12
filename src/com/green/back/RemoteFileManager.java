package com.green.back;

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
