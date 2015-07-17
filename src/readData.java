import java.io.*;

public class readData
{
	 static String fileName = "data.ser";
	
	public readData()
	{		
		
	}
		
	public void seralize(data d) throws Exception
	{
		
		
		 File f =  new File(fileName);
		 OutputStream file = new FileOutputStream(f );
	      OutputStream buffer = new BufferedOutputStream( file );
	      ObjectOutput output = new ObjectOutputStream( buffer );
	      	      
	      output.writeObject(d);
	      output.close();
	      
	      
	}
	public data deSeralize() throws Exception	
	{
		data l ;
		try{
	    InputStream file = new FileInputStream(fileName);
	    InputStream buffer = new BufferedInputStream( file );
	    ObjectInput input = new ObjectInputStream ( buffer );  
	    		  
	    
	    l =  (data)input.readObject(); 		
	    
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