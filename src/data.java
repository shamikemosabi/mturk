import java.io.*;
import java.util.*;

/*
 * this class will store all the list of hits that we have done
 */

public class data implements Serializable
{
	private ArrayList<String> array;
	
	
	public data() throws Exception
	{
		if(array == null)
		{
			init();
		}		
	}
	
	
	public void init() throws Exception	
	{
		array = new ArrayList<String>();
	}
	
	public ArrayList<String> getArray()
	{
		return array;
	}
	
	
}