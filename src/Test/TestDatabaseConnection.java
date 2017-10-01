package Test;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;

import com.green.back.DatabaseConnection;

public class TestDatabaseConnection {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	
	@Before
	public void setup() {
		databaseConnection.createUser("testUser", "tt", "tt@gmail.com");
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void cleanup() {
		databaseConnection.deleteUser("testUser");
	}
	
	@Test
	public void TestCreateUserFail() {
		String result = databaseConnection.createUser("testUser", "password", "ttt");
		Assert.assertEquals("Username already exists.", result);
		result = databaseConnection.createUser("ttt" , "password", "tt@gmail.com");
		Assert.assertEquals("Account with this email already exists.", result);
	}
	
	@Test
	public void TestLoginSuccess() {
		boolean loggedin = databaseConnection.login("testUser", "tt");
		Assert.assertTrue(loggedin);
	}
	
	@Test
	public void TestLoginUsernameFail() {
		boolean loggedin = databaseConnection.login("notUser", "tt");
		Assert.assertFalse(loggedin);
	}
	
	@Test
	public void TestLoginPasswordFail() {
		boolean loggedin = databaseConnection.login("testUser", "tt");
		Assert.assertFalse(loggedin);
	}

}
