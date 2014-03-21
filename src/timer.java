import java.util.*;

public class timer extends TimerTask
{
	
	
	public void run()
	{
		try{
		main obj  = new main();		
	//	obj.Do();
		

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void main(String[] args)
	{
		TimerTask task = new timer();
		
		Timer timer = new Timer();
		
		//timer.scheduleAtFixedRate(task,new Date(), 180000); //3 min
		timer.scheduleAtFixedRate(task,new Date(), 60000); //1 min
	}	
}