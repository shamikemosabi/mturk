/*
 * 3/21 -test
 * 3/10- changed server name to server.centivized.com
 * 		 Added check box to make sound optional
 * 		 Added check boc to with ability to pause program  
 * 3/3 -  added dateformat3 for "M/d"
 * 2/26 - added dateformat2 for "M/dd"
 * 2/14 - Fixed scrollbar issue
 *
 * 
 * 2/13 - Added sound when new hit is found
 *      - Made the timer thread class to update time every second. 
 *      - embed sound into jar file
 *      
 *      
 * TODO: - way to open up blah2.html
 * 		 - make time lbl dependent on main program. (So user would know if it crashed)
 *       - Maybe alert for special requesters? (andyK, Acme, etc...)
 *       - it'll be nice to clear individual link blocks. (need a lot of work basically redesign whole thing)
 */


import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;


public class main extends TimerTask 
{
	data myData ;
	readData read;
	
	static window w;
	
	boolean test = true;
	
	public main() throws Exception
	{

		//w = window.getInstance();
				
		read = new readData(); //object used to seralize and deseralize
		

	//	TurkForum();
	//	RedditHWTF(); FUCK REDDIT
		
	
		
	}	
	
	public void RedditHWTF() throws Exception
	{
		ArrayList<String> list =  new ArrayList<String>();
		String url ="http://www.reddit.com/r/HITsWorthTurkingFor/new/";
		URL pageURL = new URL(url); 
		HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
		urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();
	
		
		InputStream in = new BufferedInputStream(urlConnection.getInputStream()); 
		PrintWriter pw = new PrintWriter(new FileWriter("blah3.html"));
		Reader r = new InputStreamReader(in);
		
		int c;
		 while((c = r.read()) != -1) 
	        {         	
	           pw.print(String.valueOf((char)c)); 
	        } 
	        r.close();
			pw.close();
			
			BufferedReader reader = new BufferedReader(new FileReader("blah3.html"));	
			String s;
			while((s = reader.readLine()) != null)
			{	
				s = s.trim();
				
				if(s.startsWith("</div></form><div class=\"bottom\">"))
				{
					
					//StringTokenizer st = new StringTokenizer(s,"<a class=\"title");
					StringTokenizer st = new StringTokenizer(s,"<");
				     while (st.hasMoreTokens()) {
				    	 String test = st.nextToken();
				    	 test = test.trim();
				    	 
				    	 if(test.startsWith("a class=\"title")) // This is going to get all the thread on the new page
				    	 {
				    		 //we need to grab the href link:
				    		 test = test.substring(test.indexOf("href=\"")); //trim the crap before href
				    		 test = test.substring(test.indexOf("href=\""), test.indexOf("\"", 50)); // 50 to insure we hit the " we want
				    		 test = test.replace("href=\"", "");
				    		 
				    		 list.add("http://www.reddit.com"+test);
				    	 }
				     }
				}
				
			}
			
			//we now have our list array with all the thread links.
			
			
		
	}
	public void TurkForum() throws Exception
	{		
		String todayLink = getTodayLink("http://server.centivized.com/forumdisplay.php?30-Great-HITS");
		todayLink = window.getInstance().getURL().equals("") ? todayLink : window.getInstance().getURL();
	//	todayLink =  "showthread.php?13640-Can-t-Find-FUN-HIT-s-01-30-Super-Funbowl-Friday!!";
		if(!todayLink.equals(""))
		{
			processPage("http://server.centivized.com/"+todayLink+"/page1000"); //1000 so its greater so it's always the last page
			
		}
		else
		{
			//System.out.println("error grabbing today's thread");
			window.getInstance().addText("error grabbing today's thread");
		}
		
		window.getInstance().setLblTime();
	}
	
	/*
	 * Reads the thread page, searches each post for mturk link.
	 * 
	 */
	public String processPage(String u) throws Exception
	{
		
		
		String url = u;
		//String url = "http://mturkforum.com/showthread.php?6244-Can-t-Find-Good-HITs-2-10/page100";
		URL pageURL = new URL(url); 
		HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
		urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();
		
		
		InputStream in = new BufferedInputStream(urlConnection.getInputStream()); 
		PrintWriter pw = new PrintWriter(new FileWriter("blah2.html"));
		
		Reader r = new InputStreamReader(in);
	
		int c;
		 while((c = r.read()) != -1) 
	        {         	
	           pw.print(String.valueOf((char)c)); 
	        } 
	        r.close();
			pw.close();
			
			
			
		boolean newLink = false;	
		//after writing into file we will read it.
			
		BufferedReader reader = new BufferedReader(new FileReader("blah2.html"));	
		String s;
		while((s = reader.readLine()) != null)
		{
			String temp = s.toLowerCase().trim(); //let's trim it first
			
			if(temp.startsWith("<blockquote class=\"postcontent restore \">"))
			{
				boolean hit = false;
				ArrayList<String> text = new ArrayList<String>();
				String hitLink = "";
				while(!temp.equals("</blockquote>")) // keep looping all the text the poster did store them
				{
					temp =reader.readLine().trim();
					
					if(temp.contains("<a href=\"https://www.mturk.com/mturk/preview") || temp.contains("<a href=\"https://www.mturk.com/mturk/accept") || temp.contains("<a href=\"https://www.mturk.com/mturk/searchbar") ) //we found a mturk link posible hit!
					{
						hit = true;	
						//gotta clean up the string
						
						String temp2 = "";
						temp2 = temp.replace("&amp;", "&");
										
						temp2 = temp.substring(temp.indexOf("<a href=\"https:")); // lets trim the crap before <a href
						temp2 = temp2.substring(temp2.indexOf("https:"), temp2.indexOf("\"", 50)); // 50 is to ensure we pass all the " and the next " should be the end quotation
						hitLink = temp2;
						
					}
					text.add(temp);
				}
				
				// if hit = true then we have to do more stuff, if not we keep looping
				if(hit)
				{
					// we gotta match the link with our records to see if we've sent it before
					newLink = checkIfLinkExist(text, hitLink);
				}
				
				//reset hit and text
				hit = false;
				text.clear();
			}
			
			
		}
		
		if(newLink && window.getInstance().PlaySound())
		{
			sound newSound = new sound();
			try{
				newSound.playSound("traffic.wav");
			}
			catch(Exception e)
			{
				// problem playing sound
				window.getInstance().addText("Error playing sound");
				e.printStackTrace();
			}
		}
		
		reader.close();
		
		//delete file
		File newFile = new File("blah2.html");
		if(newFile.exists()&& (!test))
		{
			newFile.delete();

		}
		return "";
	}
	
	
	// This method will check to see if our link already exist in our record
	// if it already does then we don't do anything.
	public boolean checkIfLinkExist(ArrayList<String> a, String l) throws Exception
	{		
		
		
		boolean newLink = false;
		// I don't know if I need to check to see if we have data already or not.
		// reason was because I didn't want to spawn an email every time process run,
		// but now that i have a display screen it doesn't matter
				
		myData  = read.deSeralize();
		if(!myData.getArray().contains(l)) // we don't have the hit need to send out email
		{
			//option to send email out			
//			/new SendEmail(a, l);
						
			w.addLinkToPanel(a,l);
			
			// add the link to our list
			myData.getArray().add(l);
			
			read.seralize(myData); //Write back data class
					
			newLink = true;
		}
		
		return newLink;

	}
	
	/*
	 * this MEthod will get todays link.
	 * It will take the thread name "Can't Find Good HITs?"
	 * grab the dates and find the latest one. and will compare it to todays date for validation
	 */

	public String getTodayLink(String u) throws Exception
	{
		
		
		String url = u;
		//String url = "http://mturkforum.com/showthread.php?6244-Can-t-Find-Good-HITs-2-10/page100";
		URL pageURL = new URL(url); 
		HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
		urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();
		
		
		InputStream in = new BufferedInputStream(urlConnection.getInputStream()); 
		PrintWriter pw = new PrintWriter(new FileWriter("blah.html"));
		
		Reader r = new InputStreamReader(in);
	
		int c;
		 while((c = r.read()) != -1) 
	        {         	
	        	//System.out.print(String.valueOf((char)c));
	           pw.print(String.valueOf((char)c)); 
	        } 
	        r.close();
			pw.close();
			
			
			
		//after writing into file we will read it.
			
		BufferedReader reader = new BufferedReader(new FileReader("blah.html"));	
		String s;
		String ret="";
	    DateFormat dateFormat = new SimpleDateFormat("MM/dd");
	    DateFormat dateFormat2 = new SimpleDateFormat("M/dd");
	    DateFormat dateFormat3 = new SimpleDateFormat("M/d");
	    Date d = new Date();
	  
	    
	    
		while((s = reader.readLine()) != null)
		{
			
			// if it also contains todays date in the thread title
			if(s.trim().startsWith("<a class=\"title\"")  && (s.toLowerCase().contains("can't find good hits?") 
					|| s.toLowerCase().contains(dateFormat.format(d))
					|| s.toLowerCase().contains(dateFormat2.format(d))
					|| s.toLowerCase().contains(dateFormat3.format(d))
					))
			{
				
				String date = "";
				if(!(s.toLowerCase().contains(dateFormat.format(d)) || s.toLowerCase().contains(dateFormat2.format(d))
						|| s.toLowerCase().contains(dateFormat3.format(d)) ))
				{
					date = s.substring(s.toLowerCase().indexOf("can't find good hits?"), s.indexOf("</a>"));
					date = date.toLowerCase();
				    date = date.replace("can't find good hits? ", "")  ;	
				}
				
			    
			    // our date now should look like 2/11			    
			   // now we get current date.
			    
			   // DateFormat dateFormat = new SimpleDateFormat("M/dd");
			    //Date d = new Date();
		    
			    if(dateFormat.format(d).equals(date) || s.toLowerCase().contains(dateFormat.format(d))
			    		||s.toLowerCase().contains(dateFormat2.format(d))
			    		||s.toLowerCase().contains(dateFormat3.format(d))
			    		) //compare if it's the same
			    {
			    	//It's today's date. we need to grab the url in <href=...
			    	
			    	 ret  = s.substring(s.indexOf("href="), s.indexOf("&amp")) ;
			    	ret =  ret.replace("href=\"", "");
			    	break;
			    }
			    
				
			}
		}
		reader.close();
		
		File newFile = new File("blah.html");
		if(newFile.exists()&& (!test))
		{
			newFile.delete();			
		}

			
		// if we don't find anything return empty which means error 
		return ret;
		
	}
	
	public void run()
	{
		try{

		if(!window.getInstance().Pause())
		{
			this.TurkForum();			
			
		}
	//	obj.Do();
		

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * subclass
	 * @author dhwang
	 *
	 */
	public class timeThread extends TimerTask {
		
		 public void run() {
			 window.getInstance().setLblTime();
		 }
		 
	
	}
	
	
	
	public static void main(String[] args)
	{
		try{			
			w = window.getInstance();
			
			Timer timer = new Timer();
			
			main obj = new main();
			
			timer.scheduleAtFixedRate(obj,new Date(), 60000);
			

				
		}
		catch(Exception e)
		{
			window.getInstance().addText(e.getMessage());						
		}
		
		
		
	}
	
}

