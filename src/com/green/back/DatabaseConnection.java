package com.green.back;

import java.util.List;

import almonds.FindCallback;
import almonds.Parse;
import almonds.ParseException;
import almonds.ParseObject;
import almonds.ParseQuery;

public class DatabaseConnection {
	
	private static String username;
	
	public DatabaseConnection() {
		Parse.initialize("CbGNJpqOo5rR0lXyExrmkGrruyWmPdW9fAW1kQfm", "iwW69K8wA25MWDnJT7ZQKSx32YZLaGNEdU2N5VM3");
		username = "";
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
		List<ParseObject> results = null;
		try {
			results = query.find();
			
			for(ParseObject po: results) {
				if(po.getString("password").compareTo(password) == 0 ) {
					this.username = username;
					return true;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		
		return false;
		
	}
	
	public void saveAccount(String accessToken) {
		ParseObject accountObject = new ParseObject("Accounts");
		accountObject.put("username", this.username);
		accountObject.put("dbxAccessToken", accessToken);
		accountObject.saveInBackground();
	}
	
	public void getAccounts(String username, FindCallback callback) {
		ParseQuery query = new ParseQuery("Accounts");
		query.whereEqualTo("username", username);
		query.findInBackground(callback);
	}
	
}
