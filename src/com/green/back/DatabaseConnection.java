package com.green.back;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

import almonds.FindCallback;
import almonds.Parse;
import almonds.ParseException;
import almonds.ParseObject;
import almonds.ParseQuery;

public class DatabaseConnection {

	private static String username = "";
	private static ConcurrentHashMap<String, DbxClient> clients = new ConcurrentHashMap<String, DbxClient>();

	//Dropbox API Keys
	private static final String APP_KEY = "";
	private static final String APP_SECRET = "";
	
	//Parse API Keys
	private static final String APP_ID = "";
	private static final String REST_KEY = "";
	
	private final DbxRequestConfig config = new DbxRequestConfig(
			"InfiniteDrop/1.0", Locale.getDefault().toString());
	private final DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
	private DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
	private final static String ROOT_HASH = "d41d8cd98f00b204e9800998ecf8427e";

	public DatabaseConnection() {
		Parse.initialize(APP_ID, REST_KEY);
	}

	public String createUser(String username, String password, String email) {
		ParseQuery queryUsername = new ParseQuery("User");
		queryUsername.whereEqualTo("username", username);
		ParseQuery queryEmail = new ParseQuery("User");
		queryEmail.whereEqualTo("email", email);
		try {
			// TODO: Switch to Enums.
			if (queryUsername.find().size() > 0)
				return "Username already exists.";
			if (queryEmail.find().size() > 0)
				return "Account with this email already exists.";
		} catch (ParseException e) {
			e.printStackTrace();
		}

		ParseObject userObject = new ParseObject("User");
		userObject.put("username", username);
		userObject.put("password", password);
		userObject.put("email", email);
		userObject.saveInBackground();
		return "Account created successfully";
	}

	public void deleteUser(String username) {
		ParseQuery query = new ParseQuery("User");
		query.whereEqualTo("username", username);
		List<ParseObject> results = null;
		try {
			results = query.find();
			for (ParseObject po : results) {
				po.delete();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public boolean signUp(String username, String password, String email) {
		ParseObject newUser = new ParseObject("User");
		newUser.put("username", username);
		newUser.put("password", CombinedFileManager.getHash(password));
		newUser.put("email", email);
		try {
			newUser.save();
			return true;
		} catch (ParseException e1) {
			e1.printStackTrace();
			return false;
		}
	}

	public boolean login(String username, String password) {
		ParseQuery query = new ParseQuery("User");
		query.whereEqualTo("username", username);
		query.whereEqualTo("password", password);
		List<ParseObject> results = null;
		try {
			results = query.find();

			if (!results.isEmpty()) {
				DatabaseConnection.username = username;
				loginDbx(getAccounts());
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}

		return false;

	}

	private void loginDbx(List<ParseObject> accounts) {

		for (ParseObject po : accounts) {
			DbxClient client = new DbxClient(config, po.getString("dbxAccessToken"));
			DatabaseConnection.clients.put(po.getString("dbxAccessToken"),
					client);
		}
	}
	
	public String getFileName(String hash) {
		ParseQuery query = new ParseQuery("Files");
		query.whereEqualTo("hash", hash.toUpperCase());
		try {
			List<ParseObject> results = query.find();
			if(results.size() > 0) 
				return results.get(0).getString("file");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public ParseObject getFileRecordFromHash(String hash) {
		ParseQuery query = new ParseQuery("Files");
		query.whereEqualTo("hash", hash.toUpperCase());
		try {
			List<ParseObject> results = query.find();
			if(results.size() > 0) 
				return results.get(0);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public synchronized void saveFile(String file, String parent, String accnt) {

		String hash = CombinedFileManager.getHash(file);
		ParseQuery query = new ParseQuery("Files");
		query.whereEqualTo("username", DatabaseConnection.username);
		query.whereEqualTo("hash", hash);

		try {
			List<ParseObject> results = query.find();

			if (results.size() < 1) {
				ParseObject fileObject = new ParseObject("Files");
				fileObject.put("username", DatabaseConnection.username);
				fileObject.put("hash", hash);
				fileObject.put("file", file);
				fileObject.put("parent", parent);
				fileObject.put("dbxAccount", accnt);
				fileObject.save();
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String  deleteFile(String file) {
		if (file.compareTo(ROOT_HASH) == 0)
			return null;

		ParseQuery queryChildren = new ParseQuery("Files");
		queryChildren.whereEqualTo("username", DatabaseConnection.username);
		queryChildren.whereEqualTo("parent", file);
		try {
			List<ParseObject> results = queryChildren.find();
			for (ParseObject poChild : results) {
				deleteFile(poChild.getString("hash"));
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ParseQuery query = new ParseQuery("Files");
		query.whereEqualTo("username", DatabaseConnection.username);
		query.whereEqualTo("hash", file);
		try {
			List<ParseObject> results = query.find();
			String client = results.get(0).getString("dbxAccount");
			results.get(0).delete();
			return client;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public void saveAccount(String userId, String accessToken) {
		ParseObject accountObject = new ParseObject("Accounts");
		accountObject.put("username", DatabaseConnection.username);
		accountObject.put("userId", userId);
		accountObject.put("dbxAccessToken", accessToken);
		accountObject.saveInBackground();
	}

	public List<ParseObject> getAccounts() {
		ParseQuery query = new ParseQuery("Accounts");
		query.whereEqualTo("username", DatabaseConnection.username);
		try {
			return query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public List<DbxClient> getDbxClients() {
		ArrayList<DbxClient> ret = new ArrayList<DbxClient>();
		ret.addAll(DatabaseConnection.clients.values());
		return ret;
	}
	
	public DbxClient getDbxClient(String key) {
		return DatabaseConnection.clients.get(key);
	}

	/**
	 * Dropbox authorization
	 */
	public String startAuth() {
		return webAuth.start();
	}
	
	public void finishAuth(String code) {
		try {
			DbxAuthFinish authFinish = webAuth.finish(code);
			String accessToken = authFinish.accessToken;
			DbxClient client = new DbxClient(config, accessToken);
			clients.put(accessToken, client);
			saveAccount(authFinish.userId, accessToken);
		} catch (DbxException e) {
			e.printStackTrace();
		}
	}

}
