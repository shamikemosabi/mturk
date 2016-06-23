import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class log
{
	
	private Logger log = Logger.getLogger("log");
	private FileHandler f = null;
	
	boolean bDebugMode = true;
	
	
	public log() throws Exception
	{
		if(bDebugMode)
		{
	    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();				    						
	    	f = new FileHandler(System.getProperty("user.dir")+"\\log\\output-"+dateFormat.format(date)+".log");
			log.addHandler(f);
			SimpleFormatter formatter = new SimpleFormatter();  
		    f.setFormatter(formatter);  
		}
	}
	
	public void info(String s)
	{
		s = new Date() + " " + s;
		if(bDebugMode)
		{
			log.info(s);
			//System.out.println(s);
		}
		else
		{
			System.out.println(s);
		}
	}
	
}