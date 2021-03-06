import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class Sprite {
	int groupNumber;
	int imageNumber;
	int indexOfPreviousCopy;
	int subFileLength;
	int indexOfNextSprite;
	boolean usePrevPal;
	byte[] pcxData = null;
	ACTFile paletteData;
	public Sprite(int gNo, int imageNo, byte[] pcx)
	{
		groupNumber = gNo;
		imageNumber = imageNo;
		pcxData = pcx;
	}
	
	public Sprite(int gNo, int imageNo, int sfl, int indexOfNex,boolean usePrePal,  byte[] pcx)
	{
		groupNumber = gNo;
		imageNumber = imageNo;
		pcxData = pcx;
		indexOfNextSprite=indexOfNex;
		subFileLength = sfl;
		usePrevPal = usePrePal;
		
		
	}
	public Sprite(int gNo, int imageNo, int sfl, int indexOfNex,boolean usePrePal,  byte[] pcx, ACTFile af)
	{
		groupNumber = gNo;
		imageNumber = imageNo;
		pcxData = pcx;
		indexOfNextSprite=indexOfNex;
		subFileLength = sfl;
		usePrevPal = usePrePal;
		paletteData = af;
		
	}
	
	public void createPcxImage(String name) throws IOException
	{
		if(usePrevPal)
			name = "PALETLESS"+name;
		FileOutputStream out = new FileOutputStream("images\\"+name+"-"+groupNumber + "-"+ imageNumber+".pcx", true);
		if(pcxData[0] == 0)
			System.out.println("huh?");
		out.write(pcxData);
		out.close();
		System.out.println();
		System.out.println("Index of prev sprite: " + indexOfPreviousCopy);
		System.out.println(name+ " "+groupNumber + "-"+ imageNumber+".pcx");
		
	}
	public String verboseToString()
	{
		return "Group Number: " + groupNumber + "\n"+
				"Image Number: "+ imageNumber + "\n"+
				"Index of next sprite : " + indexOfNextSprite + "\n"+
				"Subfile Length : " + subFileLength + "\n"+
				"Using previous Palette : " + usePrevPal;
				
	}
	public void createPcxWithPallette(String name, byte[] pal)
	{
		
	}
	public byte[] getPaletteData()
	{
		if(pcxData !=null)
			return Arrays.copyOfRange(pcxData, pcxData.length-768, pcxData.length);
		else
			throw new NullPointerException("Null value in pcx data array");
	}
}
