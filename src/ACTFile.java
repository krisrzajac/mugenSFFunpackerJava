import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ACTFile {
byte[] contents = null;
public ACTFile()
{
	
}
public ACTFile(Path path) throws IOException
{
	Files.readAllBytes(path);
}
public ACTFile(byte[] data)
{
	contents = data;
}
public void setPalette(byte[] data)
{
	contents = data;
}
public byte[] getPalete()
{
	return contents;
}
}
