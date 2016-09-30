import java.util.Arrays;
import java.util.Iterator;

public class SffIterator<Sprite> implements Iterator<Object> {
	private SffFile file;
	int indexOfFirstSprite;
	public SffIterator(SffFile sf)
	{
		file = sf;
		indexOfFirstSprite = file.getIndexOfFirstSubfile();
	}
	@Override
	public boolean hasNext() {
		
		return false;
	}

	@Override
	public Object next() {
		// TODO Auto-generated method stub
		return null;
	}

}
