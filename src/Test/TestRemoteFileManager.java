package Test;

import org.junit.Test;

import com.green.back.RemoteFileManager;

public class TestRemoteFileManager {

	@Test
	public void authorizeNewAccount() {
		RemoteFileManager remoteFileManager = new RemoteFileManager("4ler1x1mc1aw2h7", "pjo9362exbzudi3");
		remoteFileManager.authorize();
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
