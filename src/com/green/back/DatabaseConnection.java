package com.green.back;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
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
	
	private String APP_KEY = "4ler1x1mc1aw2h7";
    private String APP_SECRET = "pjo9362exbzudi3";
    private DbxAppInfo appInfo;
	
	public DatabaseConnection() {
		Parse.initialize("CbGNJpqOo5rR0lXyExrmkGrruyWmPdW9fAW1kQfm", "iwW69K8wA25MWDnJT7ZQKSx32YZLaGNEdU2N5VM3");
		appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
	}
	
	public String createUser(String username, String password, String email) {
		ParseQuery queryUsername = new ParseQuery("User");
		queryUsername.whereEqualTo("username", username);
		ParseQuery queryEmail = new ParseQuery("User");
		queryEmail.whereEqualTo("email", email);
		try {
			//TODO: Switch to Enums.
			if (queryUsername.find().size() > 0) return "Username already exists.";
			if (queryEmail.find().size() > 0) return "Account with this email already exists.";
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
			for(ParseObject po: results) {
				po.delete();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public boolean login(String username, String password) {
		ParseQuery query = new ParseQuery("User");
		query.whereEqualTo("username", username);
		query.whereEqualTo("password", password);
		List<ParseObject> results = null;
		try {
			results = query.find();
			
			for(ParseObject po: results) {
				if(po.getString("password").compareTo(password) == 0 ) {
					DatabaseConnection.username = username;
					return true;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		
		return false;
		
	}
	
	public void saveFile(String file) {
		
		String hash = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5", new sun.security.provider.Sun());
			md.update(file.getBytes("UTF-8"));
			hash =  DatatypeConverter.printHexBinary(md.digest());
			

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		ParseQuery query = new ParseQuery("User");
		query.whereEqualTo("username", username);
		query.whereEqualTo("hash", hash);
		query.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> results, ParseException arg1) {
				if(results.size() < 1) {
					ParseObject fileObject = new ParseObject("Files");
					fileObject.put("username", DatabaseConnection.username);
					fileObject.put("hash", hash);
					fileObject.put("file", file);
				}
				
			}
			
		});
		
		
		
		try {
			fileObject.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	public void saveAccount(String userId, String accessToken) {
		ParseObject accountObject = new ParseObject("Accounts");
		accountObject.put("username", DatabaseConnection.username);
		accountObject.put("userId", userId);
		accountObject.put("dbxAccessToken", accessToken);
		accountObject.saveInBackground();
	}
	
	public void getAccounts(FindCallback callback) {
		ParseQuery query = new ParseQuery("Accounts");
		query.whereEqualTo("username", username);
		query.findInBackground(callback);
	}
	
	/**
	 * Dropbox authorization
	 */
	public void authorize() {
		DbxRequestConfig config = new DbxRequestConfig("InfiniteDrop/1.0", Locale.getDefault().toString());
		DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
		String authorizeUrl = webAuth.start();
		System.out.println(authorizeUrl);
		try {
			String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
			DbxAuthFinish authFinish = webAuth.finish(code);
			saveAccount(authFinish.userId, authFinish.accessToken);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		}
		
	}
	
}
