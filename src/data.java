import java.io.*;
import java.util.*;

/*
 * this class will store all the list of hits that we have done
 */

public class data implements Serializable
{
	private ArrayList<hitData> array;
	
	
	public data() throws Exception
	{
		if(array == null)
		{
			init();
		}		
	}
	
	
	public void init() throws Exception	
	{
		array = new ArrayList<hitData>();
	}
	
	public ArrayList<hitData> getArray()
	{
		return array;
	}
	
	/*
	 * loops thru my array and see if I have the matching link.
	 */
	public boolean contains(String s)
	{
		for(int i=0; i< array.size(); i++)
		{
			String currentLink = array.get(i).getLink();
			if(currentLink.equals(s))
			{
				return true;
			}
		}
		return false;
	}
	
	
	
}