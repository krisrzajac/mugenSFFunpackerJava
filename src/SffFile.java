import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/*
  Version 1.01
HEADER (512 bytes)
------
Bytes
00-11  "ElecbyteSpr\0" signature				[12]
12-15  1 verhi, 1 verlo, 1 verlo2, 1 verlo3			[04]
16-19  Number of groups						[04]
20-24  Number of images						[04]
24-27  File offset where first subfile is located		[04]
28-31  Size of subheader in bytes				[04]
32     Palette type (1=SPRPALTYPE_SHARED or 0=SPRPALTYPE_INDIV)	[01]
33-35  Blank; set to zero					[03]
36-511 Blank; can be used for comments				[476]

*/
public class SffFile implements Iterable{
	byte[] contents;
	int numberOfGroups,	totalImages, firstSubfileIndex, subheaderSize;
	boolean palleteType;
	public SffFile(byte[] c)
	{
		contents = c;
		numberOfGroups = getTotalGroups();
		totalImages = getTotalImages();
		firstSubfileIndex = getIndexOfFirstSubfile();
		subheaderSize = getSubheaderSize();
		palleteType = getPalleteType();
		
	}
	public byte[] getContents() {
		return contents;
	}
	public void setContents(byte[] contents) {
		this.contents = contents;
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    
	    return new String(hexChars);
	}
	public int getTotalGroups()
	{
		ByteBuffer bb = ByteBuffer.wrap(contents,16,19);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int i = bb.getInt();
		return i;
		
	}
	public int getTotalImages()
	{
		ByteBuffer bb = ByteBuffer.wrap(contents,20,23);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int i = bb.getInt();
		return i;
		
	}
	
	public int getIndexOfFirstSubfile()
	{
		ByteBuffer bb = ByteBuffer.wrap(contents, 24, 27);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		int i = bb.getInt();
		
		return i;
	}
	
	public int getSubheaderSize()
	{
		
		ByteBuffer bb = ByteBuffer.wrap(contents,28,31);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		int i = bb.getInt();
		
		return i;
	}
	public boolean getPalleteType()
	{
		//Byte #32     Palette type (1=SPRPALTYPE_SHARED or 0=SPRPALTYPE_INDIV)
		if(contents[32] == 0)
		{
			return false;
		}
		else
			return true;
	}
	
	
	@Override
	public java.util.Iterator iterator() {
		// TODO Auto-generated method stub
		return new SffIterator(this);
	}
	
	public void printFirstImageNaive() throws IOException
	{
		
		
		}
	/*
	SUBFILEHEADER (32 bytes)
	-------
	Bytes
	00-03 File offset where next subfile in the "linked list" is	[04] 
	      located.  Null if last subfile

	04-07 Subfile length (not including header)			[04]
	      Length is 0 if it is a linked sprite
	08-09 Image axis X coordinate					[02]
	10-11 Image axis Y coordinate					[02]
	12-13 Group number						[02]
	14-15 Image number (in the group)				[02]
	16-17 Index of previous copy of sprite (linked sprites only)	[02]
	      This is the actual
	18    True if palette is same as previous image			[01]
	19-31 Blank; can be used for comments				[14]
	32-   PCX graphic data. If palette data is available, it is the last
	      768 bytes.
	\*--------------------------------------------------------------------------*/

	public int[] getAllIndex() throws IOException
	{
		
		
		ByteBuffer bb = ByteBuffer.wrap(contents);
		bb.position(firstSubfileIndex);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		int thisFileIndex = firstSubfileIndex,
			nextFileIndex = bb.getInt();
		
		byte[] palData;
		ArrayList<Integer> allSubfileIndex = new ArrayList<Integer>();
		while(nextFileIndex <contents.length && nextFileIndex !=0)
		{
			System.out.println();
			System.out.println("this file index : " + thisFileIndex);
			System.out.println("this file index 0xFF : " +  Integer.toHexString(thisFileIndex));
			
			//00-03 File offset where next subfile in the "linked list" is	[04] 
		    //located.  Null if last subfile
			//byes 0-3
			System.out.println("next file index : " + nextFileIndex);
			System.out.println("next file index 0xFF : " + Integer.toHexString(nextFileIndex));
			
			//04-07 Subfile length (not including header)			[04]
		    //  Length is 0 if it is a linked sprite
			//bytes 4-7
			int subFileLength = bb.getInt();
			System.out.println("subfile length : " + subFileLength);
			System.out.println("subfile length 0xFF : " + Integer.toHexString(subFileLength));
			
			//08-09 Image axis X coordinate	
			//08-09, short takes 2 bytes - perfect!
			short xAxisCoord = bb.getShort();
			
			//10-11
			short yAxisCoord = bb.getShort();
			
			//12-13 group number
			short groupNum = bb.getShort();
			
			//14-15 Image number within group
			short imageNum = bb.getShort();
			
			//16-17 index of previous copy if linked
			short prevCopyIndex = bb.getShort();
			
			//18
			byte paletteSameByte = bb.get();
			
			boolean palSame = false;
			ACTFile af = new ACTFile();
			
			if(paletteSameByte !=0)
			{
				palSame =true;
			}
			
			allSubfileIndex.add(thisFileIndex);
			
			if(subFileLength !=0)
			{
				
				bb.position(thisFileIndex+32);
				byte[] pcxData = new byte[subFileLength+1];
				
				
				bb.get(pcxData,0,subFileLength);
				Sprite tempSprite = new Sprite(groupNum,imageNum,subFileLength,nextFileIndex,palSame, 
						pcxData);
				tempSprite.createPcxImage("sent");
			}
			thisFileIndex = nextFileIndex;
			
			//00-03 File offset where next subfile in the "linked list" is	[04] 
		    //located.  Null if last subfile
			//byes 0-3
			bb.position(nextFileIndex);
			nextFileIndex = bb.getInt();
		}
		/*
		 * 
http://stackoverflow.com/questions/718554/how-to-convert-an-arraylist-containing-integers-to-primitive-int-array
If you are using java-8 there's also another way to do this.

int[] arr = list.stream().mapToInt(i -> i).toArray();
What it does is:

getting a Stream<Integer> from the list
obtaining an IntStream by mapping each element to itself (identity function), unboxing the int value hold by each Integer object (done automatically since Java 5)
getting the array of int by calling toArray
You could also explicitly call intValue via a method reference, i.e:

int[] arr = list.stream().mapToInt(Integer::intValue).toArray();
It's also worth mentioning that you could get a NullPointerException if you have any null reference in the list. This could be easily avoided by adding a filtering condition to the stream pipeline like this:

                       //.filter(Objects::nonNull) also works
int[] arr = list.stream().filter(i -> i != null).mapToInt(i -> i).toArray();
Example:

List<Integer> list = Arrays.asList(1, 2, 3, 4);
int[] arr = list.stream().mapToInt(i -> i).toArray(); //[1, 2, 3, 4]

list.set(1, null); //[1, null, 3, 4]
arr = list.stream().filter(i -> i != null).mapToInt(i -> i).toArray(); //[1, 3, 4]
		 */
		return allSubfileIndex.stream().mapToInt(i->i).toArray();
		
		
	}
	
}
