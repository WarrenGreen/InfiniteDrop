package Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.AbstractMap.SimpleEntry;

import com.green.back.LocalFileManager;

import org.junit.Assert;
import org.junit.Test;

public class TestLocalFileManager {
	private LocalFileManager localFileManager;
	
	@Test
	public void TestFileCreate() {
		String testFile = "/Users/wsgreen/test.txt";
		localFileManager = new LocalFileManager("/Users/wsgreen/");
		
		Thread t = new Thread(localFileManager, "test");
		t.start();
		
		try {
			PrintWriter writer = new PrintWriter(testFile, "UTF-8");
			writer.println("The first line");
			writer.println("The second line");
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SimpleEntry<Path, WatchEvent<Path>> event = null;
		try {
			event = localFileManager.takeWatchEvent();
			Assert.assertEquals("test.txt", event.getValue().context().toString());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			Assert.fail();
		}
		
		try {
			Files.delete(Paths.get(testFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
