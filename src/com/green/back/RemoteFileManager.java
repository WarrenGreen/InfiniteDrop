package com.green.back;

import com.dropbox.core.*;

import java.io.*;
import java.util.Locale;

public class RemoteFileManager {
	private String APP_KEY;
    private String APP_SECRET;
    private DbxAppInfo appInfo;
    private DatabaseConnection databaseConnection;

	public RemoteFileManager(String AppKey, String AppSecret) {
		APP_KEY = AppKey;
		APP_SECRET = AppSecret;
		appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
		databaseConnection = new DatabaseConnection();
		
	}
	
	public void authorize() {
		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
		DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
		String authorizeUrl = webAuth.start();
		System.out.println(authorizeUrl);
		try {
			String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
			DbxAuthFinish authFinish = webAuth.finish(code);
			String accessToken = authFinish.accessToken;
			databaseConnection.saveAccount(accessToken);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		}
		
	}
	
	public void login(String accessToken) {
		
	}
}
