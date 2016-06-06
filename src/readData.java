import java.io.*;

public class readData
{
	  String fileName = "";
	  boolean lock = false;
	 
	 /*
	  * we now can specify different files.
	  */
	public readData(String f)
	{		
		fileName = f;
	}
	
	public String getFileName()
	{
		return fileName;
	}
		
	public void seralize(data d) throws Exception
	{
		
		
		 File f =  new File(fileName);
		 OutputStream file = new FileOutputStream(f );
	      OutputStream buffer = new BufferedOutputStream( file );
	      ObjectOutput output = new ObjectOutputStream( buffer );
	      	      
	      output.writeObject(d);
	      output.close();
	      file.close();
	      buffer.close();
	      
	      // set lock to false after writing to file.
	      lock = false;
	      
	      
	}
	public data deSeralize() throws Exception	
	{
		//I now have concurrent deSeralize/seralize, I need a way to "queue" it.
		// if lock then hold off until it's unlock.
		
		/*while(lock) // if lock 
		{
			//System.out.println("LOCKED "+ src);
			//Thread.sleep(2000);
			
		}*/
		
		// we are clear to deseralize, first thing is to lock so other transaction gets stuck
		lock = true;
		data l ;
		try{
	    InputStream file = new FileInputStream(fileName);
	    InputStream buffer = new BufferedInputStream( file );
	    ObjectInput input = new ObjectInputStream ( buffer );  
	    		  
	    
	    l =  (data)input.readObject(); 		
	    
	    file.close();
	    buffer.close();
	    input.close();
	    
		}
		catch(FileNotFoundException fnfe) // returns a brand new Data, if data.ser does not exist, first time
		{
			return new data();
		}
		  
		  return l;

	}
	
	public void DeleteDataFile()
	{
		File newFile = new File(fileName);
		if(newFile.exists())
		{
			newFile.delete();
		}
		
	}
}