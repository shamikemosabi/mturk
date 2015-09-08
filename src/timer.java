import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class timer extends TimerTask
{
	public static boolean runHWTF = true;
	
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
		try{
			URL url = new URL("http://checkip.amazonaws.com/");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			System.out.println(br.readLine());
			br.close();
			
		//	test();
			
			main task = new main();
			task.doRedditHWTF();
			task.doForum();
			task.doMturkList();
			task.doWriteFTP();
			//task.test();
		}
		catch(Exception e)
		 {
		 	System.out.println(e.getMessage());
		 	e.printStackTrace();
		 }
	 
		
	}	
	
	public static void test() 
	{
		try{
			Document doc = Jsoup.connect("http://turkernation.com/forumdisplay.php?157-Daily-HIT-Threads&s=0e04b86d371223291653dd430053a207").userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3").get();			
			
			Elements link  = doc.getElementsByClass("threadtitle");
			Elements links = doc.select("a[href]");
			
			 for (Element b : links) {
				 String linkHref = b.attr("href"); // "http://example.com/"
				 String linkText = b.text(); // "example""
				 
				 	System.out.println(linkHref);

		       }

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/*
	 * need to randomly set time
	 * base on current time, to not extensively run every minute on the dot.
	 */
	public static int timeInterval()
	{
		int min = 60000;
		Random rand = new Random();
		int mod1 =0;
		int mod2 =0;
		//	min = min* (rand.nextInt(3)+1);
		// System.out.println(min);
		try{	
			if(isTimeBetweenTwoTime("03:00:00","04:00:00")) //3 AM - 4 AM
			{
			System.out.println(new Date() +" Between 3 - 4");
				mod1 = rand.nextInt(30 - 10)+10;				
				mod2 = rand.nextInt(60)+1;
								
				System.out.println(mod1 + " " + mod2);
				
				min =  (min * mod1) + (mod2*1000);
				
				
			}
			else if(isTimeBetweenTwoTime("04:00:00","05:30:00")) //4 AM - 5:30 AM
			{
			
				System.out.println(new Date() +" Between 4 - 5");
				mod1 = rand.nextInt(40 - 10)+10;				
				mod2 = rand.nextInt(60)+1;
								
				System.out.println(mod1 + " " + mod2);
				
				min =  (min * mod1) + (mod2*1000);
				
				
			}
			else if(isTimeBetweenTwoTime("05:30:00","07:00:00")) 
			{
			
				System.out.println(new Date() +" Between 5:30 - 7");
				mod1 = rand.nextInt(20 - 10)+10;				
				mod2 = rand.nextInt(60)+1;
								
				System.out.println(mod1 + " " + mod2);
				
				min =  (min * mod1) + (mod2*1000);
				
				
			}
			else if(isTimeBetweenTwoTime("07:00:00","17:00:00")) //7 am - 5 pm
			{
				System.out.println(new Date() +" Between 7 - 5");
				mod1 = 1;
				mod2 = rand.nextInt(30)+1;
				
				System.out.println(mod1 + " " + mod2);
				
				min =  (min * mod1) + (mod2*1000);
																
				
			}	
			else if(isTimeBetweenTwoTime("17:00:00","22:00:00")) //5 pm - 10 pm
			{
				System.out.println(new Date() +" Between 5 - 10");
				mod1 = rand.nextInt(3)+1;
				mod2 = rand.nextInt(30)+1;
				
				System.out.println(mod1 + " " + mod2);
				
				min =  (min * mod1) + (mod2*1000);
								
				
			}	
			else if(isTimeBetweenTwoTime("22:00:00","24:00:00")) //10 pm - 12 AM
			{
				
				System.out.println(new Date() +" Between 10 - 12 am");
				mod1 = rand.nextInt(15 - 5)+5;				
				mod2 = rand.nextInt(60)+1;
								
				System.out.println(mod1 + " " + mod2);
				
				min =  (min * mod1) + (mod2*1000);			
			}		
			else if(isTimeBetweenTwoTime("24:00:00","03:00:00")) //12 am - 3 am
			{
				System.out.println(new Date() +" Between 1am - 3 am");
				mod1 = rand.nextInt(15 - 5)+5;				
				mod2 = rand.nextInt(60)+1;
								
				System.out.println(mod1 + " " + mod2);
				
				min =  (min * mod1) + (mod2*1000);
					
			}
	
			
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println((float)min/60000);

		return min;
		
	}
	
	/*
	 *mi - lower value
	 *ma - higher value
	 *
	 * ex. 5,10
	 * 
	 * return random time interval between 5 mins and 10 mins.
	 */
	public static int timeInterval(int mi, int ma)
	{
		int min = 60000;
		Random rand = new Random();
		int mod1 =0;
		int mod2 =0;
		//	min = min* (rand.nextInt(3)+1);
		// System.out.println(min);
		try{	
			mod1 = rand.nextInt(ma - mi)+mi;				
			mod2 = rand.nextInt(60)+1;
			System.out.println(mod1 + " " + mod2);
			min =  (min * mod1) + (mod2*1000);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println((float)min/60000);
		return min;
	}
	
	
	public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime) throws Exception {
		
		  Date d = new Date();		   		   
		  SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");		   
          String currentTime = sdf.format(d.getTime());	    
		
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";

            boolean valid = false;
            //Start Time
            java.util.Date inTime = new SimpleDateFormat("HH:mm:ss").parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            java.util.Date checkTime = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            java.util.Date finTime = new SimpleDateFormat("HH:mm:ss").parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if (finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();
            if ((actualTime.after(calendar1.getTime()) || actualTime.compareTo(calendar1.getTime()) == 0) 
                    && actualTime.before(calendar2.getTime())) {
                valid = true;
            }
            return valid;

    }
	
	
}