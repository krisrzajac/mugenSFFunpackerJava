import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class testSffFile {

	@Test
	public void test() {
		Path path = Paths.get("sentinel.sff");
		Path actPath = Paths.get("shadowslaught.act");
		System.out.println(path.toString());
		try {
			SffFile file = new SffFile(Files.readAllBytes(path));
			ACTFile palleteTest = new ACTFile(Files.readAllBytes(actPath));
			assertEquals(32,file.getSubheaderSize());
			assertEquals(Integer.parseInt("200", 16), file.getIndexOfFirstSubfile());
			
			file.getAllIndex();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
