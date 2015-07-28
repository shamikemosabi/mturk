/*
 * 4/30 - change back to http://mturkforum.com/
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
 * TODO: - add autoaccept preview link 
 *		 - way to open up blah2.html
 * 		 - make time lbl dependent on main program. (So user would know if it crashed)
 *       - Maybe alert for special requesters? (andyK, Acme, etc...)
 *       - it'll be nice to clear individual link blocks. (need a lot of work basically redesign whole thing)
 */


import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import org.apache.commons.net.ftp.*;


public class main extends TimerTask 
{
	data myData ;
	readData read;
	
	readData readFull;
	
	static window w;
	
	boolean test = true;
	
	String jsonFile = "C:\\inetpub\\wwwroot\\www3\\test.aspx";
	ArrayList<String> alJson = new ArrayList<String>();
	
	public main() throws Exception
	{

	
		w = window.getInstance();
				
		read = new readData("data.ser"); //object used to seralize and deseralize
		readFull = new readData("dataFull.ser");
		
		
		turkerNation();
		//mturkGrind();
		TurkForum();
		
		if(alJson.size()>0)
		{
			System.out.println(new Date() + " New hits writing to JSON");
			writeToJSON();
			alJson.clear();
		}
		
		cleanHit();		

	//	RedditHWTF(); FUCK REDDIT
		
	
		
	}	
	
	/**
	 * Remove hits that's been over 1 hr.
	 */
	public void cleanHit() throws Exception
	{
		myData  = read.deSeralize();
		Date currDate = new Date();
		ArrayList remIndex = new ArrayList();
		
		System.out.println(new Date() + " cleaning hits");
		for(int i=0; i< myData.getArray().size(); i++)
		{
			hitData hd = myData.getArray().get(i);
			Date hitDate = hd.getDate();
			if(currDate.getTime() - hitDate.getTime() >= 3600000) // 60 mins
			{				
				remIndex.add(i);				
			}	
		}

		// loop reverse, because ArrayList remove will shift index
		for(int j = remIndex.size() - 1; j >= 0; j--){			 
			int i = (int)remIndex.get(j);
			myData.getArray().remove(i);
			
			System.out.println(new Date() + " Removing hits");
			
		}
	
		read.seralize(myData); //Write back data class
	}
	public void turkerNation() throws Exception
	{
		try
		{
			System.out.println(new Date() + " Started Turker Nation");
			String todayLink = getTodayLinkTN("http://turkernation.com/forumdisplay.php?157-Daily-HIT-Threads&s=ca61dd26c7855c91401d0d5e9201fdbf", true);
			
			if(!todayLink.equals(""))
			{
				//processPageTN("http://turkernation.com/showthread.php?25061-07-23-15-Doomsday-is-over!!!!!/page24");
				processPageTN("http://turkernation.com/"+todayLink+"/page1000"); //1000 so its greater so it's always the last page
				
			}
			else
			{
				//System.out.println("error grabbing today's thread");
				window.getInstance().addText("error grabbing TN thread");
			}
			
			window.getInstance().setLblTime();
			System.out.println(new Date() + " Finished Turker Nation");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
	}
	public void processPageTN(String u) throws Exception 
	{
		
		String url = u;
		//String url = "http://mturkforum.com/showthread.php?6244-Can-t-Find-Good-HITs-2-10/page100";
		URL pageURL = new URL(url); 
		HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
		urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();
		
		
		InputStream in = new BufferedInputStream(urlConnection.getInputStream()); 
		PrintWriter pw = new PrintWriter(new FileWriter("blahTN2.html"));
		
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
		
		
		BufferedReader reader = new BufferedReader(new FileReader("blahTN2.html"));	
		String s;
		while((s = reader.readLine()) != null)
		{
			String temp = s.toLowerCase().trim(); //let's trim it first
			
			if(temp.startsWith("<blockquote class=\"postcontent"))
			{
				boolean hit = false;
				ArrayList<String> text = new ArrayList<String>();
				String hitLink = "";
				String PandA ="";
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
						
						//could potentially be previewandaccept
						if(temp.contains("<a href=\"https://www.mturk.com/mturk/preview"))
						{
							if(temp2.toLowerCase().contains("previewandaccept")) // it's a PandA link
							{
								PandA = temp2;
							}
							else // it's just a regular preview... link. Let's converted it to previewand accept
							{
								PandA = temp2.replaceAll("preview", "previewandaccept");
							}
						}
						
					}
					if(filterPost(temp))
					{
						text.add(temp);
					}
				}
				
				//coming out, if my PandA is still empty, then that means there was no preview link, It's either an "accept" link or a "searchbar" link.
				// let's just assign that hitlink to PandA.
				if(PandA.equals(""))
				{
					PandA = hitLink;
				}				
				
				// if hit = true then we have to do more stuff, if not we keep looping
				if(hit)
				{
					// we gotta match the link with our records to see if we've sent it before
						newLink = checkIfLinkExist(text, PandA, "TN");
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
		
		
	}
	
	public String getTodayLinkTN(String u, boolean b) throws Exception
	{
		String url = u;
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
		    
		    Calendar d ;
		    if(b)
		    {
		    	   d = Calendar.getInstance();
		    }
		    else // if false then search for yesterdays date
		    {
		    	d = Calendar.getInstance();
		    	d.add(Calendar.DATE, -1);	    	 
		    }

		    
			while((s = reader.readLine()) != null)
			{
				
				//mturk grind seems very conventional. All thread starts with the below:
				if((s.trim().startsWith("<a class=\"title\" href=\"showthread."))  && 
						( s.toLowerCase().contains(dateFormat.format(d.getTime()))
						|| s.toLowerCase().contains(dateFormat2.format(d.getTime()))
						|| s.toLowerCase().contains(dateFormat3.format(d.getTime())) )
						)
				{
					
					
					String date = "";
					/*
					if(!(s.toLowerCase().contains(dateFormat.format(d.getTime())) || s.toLowerCase().contains(dateFormat2.format(d.getTime()))
							|| s.toLowerCase().contains(dateFormat3.format(d.getTime())) ))
					{
						date = s.substring(s.toLowerCase().indexOf("threads"), s.indexOf("preview"));
						date = date.toLowerCase();
					    date = date.replace("can't find good hits? ", "")  ;	
					}
					*/
				    
				    // our date now should look like 2/11			    
				   // now we get current date.
				    
				   // DateFormat dateFormat = new SimpleDateFormat("M/dd");
				    //Date d = new Date();
			    
				    if(dateFormat.format(d.getTime()).equals(date) || s.toLowerCase().contains(dateFormat.format(d.getTime()))
				    		||s.toLowerCase().contains(dateFormat2.format(d.getTime()))
				    		||s.toLowerCase().contains(dateFormat3.format(d.getTime()))
				    		) //compare if it's the same
				    {
				    	//It's today's date. we need to grab the url in <href=...
				    	
				    	s = s.trim();
				  
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
			
			if(ret.equals(""))
			{
				ret = getTodayLinkTN(u, false);
			}
			
			return ret;

			
	}
	
	public void mturkGrind() throws Exception
	{
		try{
		System.out.println(new Date() + " Started Mturk Grind");
		String todayLink = getTodayLinkMG("http://www.mturkgrind.com/forums/awesome-hits.4/", true);
		
		if(!todayLink.equals(""))
		{
			//processPageMG("http://www.mturkgrind.com/threads/07-20-masterful-monday.28298/page-68");
			processPageMG("http://mturkgrind.com/"+todayLink+"/page-1000"); //1000 so its greater so it's always the last page
			
		}
		else
		{
			//System.out.println("error grabbing today's thread");
			window.getInstance().addText("error grabbing MG thread");
		}
		
		window.getInstance().setLblTime();
		System.out.println(new Date() + " Finished Mturk Grind");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	
	
	public void processPageMG(String u) throws Exception
	{
		String url = u;
		//String url = "http://mturkforum.com/showthread.php?6244-Can-t-Find-Good-HITs-2-10/page100";
		URL pageURL = new URL(url); 
		HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
		urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();
		
		
		InputStream in = new BufferedInputStream(urlConnection.getInputStream()); 
		PrintWriter pw = new PrintWriter(new FileWriter("blahMG2.html"));
		
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
		
			
		BufferedReader reader = new BufferedReader(new FileReader("blahMG2.html"));	
		String s;
		while((s = reader.readLine()) != null)
		{
			String temp = s.toLowerCase().trim(); //let's trim it first

			if(temp.startsWith("<blockquote class="))
			{
				boolean hit = false;
				ArrayList<String> text = new ArrayList<String>();
				String hitLink = "";
				String PandA ="";
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
						
						//could potentially be previewandaccept
						if(temp.contains("<a href=\"https://www.mturk.com/mturk/preview"))
						{
							if(temp2.toLowerCase().contains("previewandaccept")) // it's a PandA link
							{
								PandA = temp2;
							}
							else // it's just a regular preview... link. Let's converted it to previewand accept
							{
								PandA = temp2.replaceAll("preview", "previewandaccept");
							}
						}						
						
					}
					if(filterPost(temp))
					{
						text.add(temp);
					}
				}
				
				//coming out, if my PandA is still empty, then that means there was no preview link, It's either an "accept" link or a "searchbar" link.
				// let's just assign that hitlink to PandA.
				if(PandA.equals(""))
				{
					PandA = hitLink;
				}				
				// if hit = true then we have to do more stuff, if not we keep looping
				if(hit)
				{
					// we gotta match the link with our records to see if we've sent it before
						newLink = checkIfLinkExist(text, PandA, "MTG");
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

	}
	
	
	/**
	 * 
	 * @param s - URL link that shows all daily hits
	 * @param b - true for today link, falst for yesterday
	 * @return - Today's thread link.
	 */
	public String getTodayLinkMG(String u, boolean b) throws Exception
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
		    
		    Calendar d ;
		    if(b)
		    {
		    	   d = Calendar.getInstance();
		    }
		    else // if false then search for yesterdays date
		    {
		    	d = Calendar.getInstance();
		    	d.add(Calendar.DATE, -1);	    	 
		    }

		    
			while((s = reader.readLine()) != null)
			{
				
				//mturk grind seems very conventional. All thread starts with the below:
				if((s.trim().startsWith("data-previewUrl=\"threads"))  && 
						( s.toLowerCase().contains(dateFormat.format(d.getTime()))
						|| s.toLowerCase().contains(dateFormat2.format(d.getTime()))
						|| s.toLowerCase().contains(dateFormat3.format(d.getTime())) )
						)
				{
					
					
					String date = "";
					/*
					if(!(s.toLowerCase().contains(dateFormat.format(d.getTime())) || s.toLowerCase().contains(dateFormat2.format(d.getTime()))
							|| s.toLowerCase().contains(dateFormat3.format(d.getTime())) ))
					{
						date = s.substring(s.toLowerCase().indexOf("threads"), s.indexOf("preview"));
						date = date.toLowerCase();
					    date = date.replace("can't find good hits? ", "")  ;	
					}
					*/
				    
				    // our date now should look like 2/11			    
				   // now we get current date.
				    
				   // DateFormat dateFormat = new SimpleDateFormat("M/dd");
				    //Date d = new Date();
			    
				    if(dateFormat.format(d.getTime()).equals(date) || s.toLowerCase().contains(dateFormat.format(d.getTime()))
				    		||s.toLowerCase().contains(dateFormat2.format(d.getTime()))
				    		||s.toLowerCase().contains(dateFormat3.format(d.getTime()))
				    		) //compare if it's the same
				    {
				    	//It's today's date. we need to grab the url in <href=...
				    	
				    	s = s.trim();
				  
				    	 ret  = s.substring(s.indexOf("threads"), s.indexOf("/preview")) ;
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
			
			if(ret.equals(""))
			{
				ret = getTodayLinkMG(u, false);
			}
			
			return ret;
			
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
		try{
		System.out.println(new Date() + " Started Mturk Forum");
		String todayLink = getTodayLink("http://mturkforum.com/forumdisplay.php?30-Great-HITS", true);
		todayLink = window.getInstance().getURL().equals("") ? todayLink : window.getInstance().getURL();
	//	todayLink =  "showthread.php?13640-Can-t-Find-FUN-HIT-s-01-30-Super-Funbowl-Friday!!";
		if(!todayLink.equals(""))
		{
			//processPage("http://mturkforum.com/showthread.php?33565-Cant-find-Great-HITS-7-20-More-Money-Monday!/page88");
			processPage("http://mturkforum.com/"+todayLink+"/page1000"); //1000 so its greater so it's always the last page
			
		}
		else
		{
			//System.out.println("error grabbing today's thread");
			window.getInstance().addText("error grabbing today's thread");
		}
		
		window.getInstance().setLblTime();
		System.out.println(new Date() + " Finished Mturk Forum");
		
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
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
				String PandA = "";
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

						//could potentially be previewandaccept
						if(temp.contains("<a href=\"https://www.mturk.com/mturk/preview"))
						{
							if(temp2.toLowerCase().contains("previewandaccept")) // it's a PandA link
							{
								PandA = temp2;
							}
							else // it's just a regular preview... link. Let's converted it to previewand accept
							{
								PandA = temp2.replaceAll("preview", "previewandaccept");
							}
						}	
						
					}
					if(filterPost(temp))
					{
						text.add(temp);
					}
				}

				//coming out, if my PandA is still empty, then that means there was no preview link, It's either an "accept" link or a "searchbar" link.
				// let's just assign that hitlink to PandA.
				if(PandA.equals(""))
				{
					PandA = hitLink;
				}
				
				// if hit = true then we have to do more stuff, if not we keep looping
				if(hit)
				{
					// we gotta match the link with our records to see if we've sent it before
						newLink = checkIfLinkExist(text, PandA, "MTF");
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
	
	/*
	 *
	 * filters post to get rid of things like images gifs
	 */
	
	public boolean filterPost(String a)
	{
		boolean ret= true;
		if(a.contains("<img src=\""))
		{
			ret = false;
		}
		
		return ret;
	}
	
	
	// This method will check to see if our link already exist in our record
	// if it already does then we don't do anything.
	public boolean checkIfLinkExist(ArrayList<String> a, String l, String source) throws Exception
	{		
		 
		boolean newLink = false;
		// I don't know if I need to check to see if we have data already or not.
		// reason was because I didn't want to spawn an email every time process run,
		// but now that i have a display screen it doesn't matter
				
		myData  = read.deSeralize();
		if(!myData.contains(l)) // we don't have the hit need to send out email
		{
			//option to send email out			
//			/new SendEmail(a, l);
						
			w.addLinkToPanel(a,l);
			
			// add the link to our list
			hitData hd = new hitData(l, new Date(), source);
			myData.getArray().add(hd);
			
			read.seralize(myData); //Write back data class
			
			//write to full data
			addSeralize(readFull,hd);
			
			
			writeToJSONPerHIT(a,l);
					
			newLink = true;
		}
		
		return newLink;

	}
	
	// searlize  hd into rd unconditionally.
	public void addSeralize(readData rd, hitData hd) throws Exception
	{
		try{
			data d = rd.deSeralize();
			d.getArray().add(hd);
			rd.seralize(d);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
	}
	
	
	public void FTP(String FileName, String dir) throws IOException
	{
		FTPClient ftp = new FTPClient();
		ftp.connect("31.170.160.105");
		ftp.login("a3097139","000webhostsucks11");
		ftp.changeWorkingDirectory(dir);
		
		File f = new File(FileName);
		final InputStream is = new FileInputStream(f.getPath());
		ftp.storeFile(f.getName(), is);
		
		ftp.disconnect();
		is.close();
		
	}
	/*
	 *  write to JSON file. file path is specified in variable jsonFile.
	 *  
	 *  will scan JSON array list.
	 *  finalize string and save to JSON file.
	 *  
	 *  Json should look like:
	 *  
	 *  {"records" :
	 *  	[ {
	 *  		post:'<br> blah blah <div> </div> etc...
	 *  		link:'http://blahblah...'
	 *  	  },
	 *  
	 *  	  { 
	 *  		post:'second post in html format'....
	 *  		link:'http://...'
	 *  	  }
	 *  	]
	 *  }
	 * 
	 */
	
	
	public void writeToJSON()
	{
		String start = "{\"records\":[";
		//String end = "]}";
		String end = "], \"date\": \""+ new Date() +"\"}";
		String str= "";
		
		for(int i=0; i < alJson.size(); i++)
		{
			str += alJson.get(i) + ",";
		}
		
		str = str.substring(0,str.length()-1); //get rid of last character which should be ","
		
		String finalString = start+str+end;
		
		//System.out.println(finalString);
		// write to file
		try
		{
			PrintWriter pw = new PrintWriter(new FileWriter(jsonFile));
			pw.print(finalString);
			pw.close();
			
			System.out.println(new Date() + "Starting FTP...");
			FTP(jsonFile,"public_html");	
			System.out.println(new Date() + "Finished FTP...");
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println(new Date() + "error FTP");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(new Date() + "error writing to JSON file");
		}
		
		
	}
	
	/*
	 * write to array list. Each index contains 1 hit, String that has {"Post":..."link":...}
	 * 
	 */
	public void writeToJSONPerHIT(ArrayList<String> a, String l)
	{
		//String start = "{\"records\":[";
		//String end = "]}";
		
		
		String str = "";
				
		str += "{\"Post\":\"";
	
		//each a.get(i) is one line of post
		for(int i=0; i< a.size(); i++)
		{
			str += a.get(i).replaceAll("\"", "'").replaceAll("\\s+", " ");
		}		
		str+="\",";		
		// add link
		str+= "\"link\":\"" + l.replaceAll("\"", "'").replaceAll("\\s+", " ");		
		str+="\"}";
		
		//System.out.println(str);
		alJson.add(str);
	}
	
	/*
	 * this MEthod will get todays link.
	 * It will take the thread name "Can't Find Good HITs?"
	 * grab the dates and find the latest one. and will compare it to todays date for validation
	 * 
	 * add new parameter b, if true then today's hit, if false then yesterday
	 */

	public String getTodayLink(String u, boolean b) throws Exception
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
	    
	    Calendar d ;
	    if(b)
	    {
	    	   d = Calendar.getInstance();
	    }
	    else // if false then search for yesterdays date
	    {
	    	d = Calendar.getInstance();
	    	d.add(Calendar.DATE, -1);	    	 
	    }

	    
		while((s = reader.readLine()) != null)
		{
			
			// if it also contains todays date in the thread title
			if(s.trim().startsWith("<a class=\"title\"")  && (s.toLowerCase().contains("can't find good hits?") 
					|| s.toLowerCase().contains(dateFormat.format(d.getTime()))
					|| s.toLowerCase().contains(dateFormat2.format(d.getTime()))
					|| s.toLowerCase().contains(dateFormat3.format(d.getTime()))
					))
			{
				
				String date = "";
				if(!(s.toLowerCase().contains(dateFormat.format(d.getTime())) || s.toLowerCase().contains(dateFormat2.format(d.getTime()))
						|| s.toLowerCase().contains(dateFormat3.format(d.getTime())) ))
				{
					date = s.substring(s.toLowerCase().indexOf("can't find good hits?"), s.indexOf("</a>"));
					date = date.toLowerCase();
				    date = date.replace("can't find good hits? ", "")  ;	
				}
				
			    
			    // our date now should look like 2/11			    
			   // now we get current date.
			    
			   // DateFormat dateFormat = new SimpleDateFormat("M/dd");
			    //Date d = new Date();
		    
			    if(dateFormat.format(d.getTime()).equals(date) || s.toLowerCase().contains(dateFormat.format(d.getTime()))
			    		||s.toLowerCase().contains(dateFormat2.format(d.getTime()))
			    		||s.toLowerCase().contains(dateFormat3.format(d.getTime()))
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
		
		if(ret.equals(""))
		{
			ret = getTodayLink(u, false);
		}
		
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
			//window.getInstance().addText(e.getMessage());		
			e.printStackTrace();
		}
		
		
		}
	
}

