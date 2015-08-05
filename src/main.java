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
import java.nio.charset.Charset;
import java.util.*;
import java.text.*;

import org.apache.commons.net.ftp.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;


public class main extends TimerTask 
{
	data myData ;
	readData read;
	
	readData readFull;
	
	static window w;
	
	boolean test = true;
	
	String jsonFile = "C:\\inetpub\\wwwroot\\www3\\test.aspx";
	ArrayList<String> alJson = new ArrayList<String>();
	
	// specifically for HWTF hits
	ArrayList<String> alJsonReddit = new ArrayList<String>();
	

	
	public main() throws Exception
	{

	
		w = window.getInstance();
				
		read = new readData("data.ser"); //object used to seralize and deseralize
		readFull = new readData("dataFull.ser");

		createExportHitLink("https://www.mturk.com/mturk/searchbar?selectedSearchType=hitgroups&requesterId=A230RE9FSQ9SE7");
		
	//	createExportHitLink("https://www.mturk.com/mturk/searchbar?selectedSearchType=hitgroups&requesterId=A1AQHR31NR4J6N"); //1 hit	
		//createExportHitLink("https://www.mturk.com/mturk/searchbar?selectedSearchType=hitgroups&requesterId=A1J2SRHRJ991YJ");  // no hit
		System.exit(0);
		/*
				mturkList();
		mturkList();
		turkerNation();
		//mturkGrind();
		TurkForum();

	
		if(alJson.size()>0)
		{
			System.out.println(new Date() + " New hits writing to JSON");
			writeToJSON(alJson);
			alJson.clear();
		}
		
		cleanHit();		

	
		*/
	
		
	}	
	
	/**
	 * Remove hits that's been over 1 hr.
	 */
	public void cleanHit() throws Exception
	{
		myData  = read.deSeralize();
		Date currDate = new Date();
		ArrayList remIndex = new ArrayList();
		
		System.out.println(new Date() + " <<FTP>> cleaning hits");
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
			
			System.out.println(new Date() + " <<FTP>> Removing hits");
			
		}
	
		read.seralize(myData); //Write back data class
	}
	
	public void doMturkList()
	{
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	 try{
		    		 
		    		 while(true)
		    		 {
		    			System.out.println(new Date() + " <<MTURK LIST>> STARTED");
		    				
		    			mturkList();
		    			
		    			System.out.println(new Date() + " <<MTURK LIST>> FINISHED");	
		    			Thread.sleep(timer.timeInterval(5,10)); //FTP every minute	
	    				
		    		 }
		    		 
		    	 }
		    	 catch(Exception e)
		    	 {
		    		 System.out.println(e.getMessage());
		    		 e.printStackTrace();
		    	 }
		     }
		});  
		t1.start();
		
		
	}
	
	public void doWriteFTP()
	{
		
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	 try{
		    		 
		    		 while(true)
		    		 {
		    			 System.out.println(new Date() + " <<FTP>> DOWRITEFTP STARTED");
		    				if(alJson.size()>0)
		    				{
		    					System.out.println(new Date() + " <<FTP>> New hits writing to JSON");
		    					writeToJSON(alJson);
		    					alJson.clear();
		    					System.out.println(new Date() + " <<FTP>> Cleared alJSON");
		    				}
		    				
		    				cleanHit();
		    				System.out.println(new Date() + " <<FTP>> DOWRITEFTP FINISHED");	
		    				Thread.sleep(60000); //FTP every minute	
	    				
		    		 }
		    		 
		    	 }
		    	 catch(Exception e)
		    	 {
		    		 System.out.println(e.getMessage());
		    		 e.printStackTrace();
		    	 }
		     }
		});  
		t1.start();
		
		

	}
	
	public void doForum()
	{
		/*
		 * reddit scraps take too long, have to thread it.
		 */
		
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	 try{
		    		 
		    		 while(true)
		    		 {
		    			 System.out.println(new Date() + " <<FORUM>> STARTED");	
		    			 turkerNation();
			    	    //mturkGrind();
		    			 TurkForum();
			    		 System.out.println(new Date() + " <<FORUM>> FINISHED");
			    		 Thread.sleep(timer.timeInterval());	

		    		 }
		    		 
		    	 }
		    	 catch(Exception e)
		    	 {
		    		 System.out.println(e.getMessage());
		    		 e.printStackTrace();
		    	 }
		     }
		});  
		t1.start();
		
	}
	
	
	
	public void doRedditHWTF()
	{
		/*
		 * reddit scraps take too long, have to thread it.
		 */
		
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	 try{
		    		 
		    		 while(true)
		    		 {
		    			 System.out.println(new Date() + " <<REDDIT>> STARTED");		
			    		 RedditHWTF();
			    		 System.out.println(new Date() + " <<REDDIT>> FINISHED");	 
			    		 Thread.sleep(60000);
			    		
		    		 }
		    		 
		    	 }
		    	 catch(Exception e)
		    	 {
		    		 System.out.println(e.getMessage());
		    		 e.printStackTrace();
		    	 }
		     }
		});  
		t1.start();
		
	}
	
	public void mturkList() throws Exception
	{
		try{
			System.out.println(new Date() + " <<MTURK LIST>> Started Mturk List");
				
			String url ="http://www.mturklist.com/";
			URL pageURL = new URL(url); 
			HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
			urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
		
			
			InputStream in = new BufferedInputStream(urlConnection.getInputStream()); 
			PrintWriter pw = new PrintWriter(new FileWriter("blahML.html"));
			Reader r = new InputStreamReader(in);
			
			int c;
			 while((c = r.read()) != -1) 
		     {         	
		           pw.print(String.valueOf((char)c)); 
		     } 
		     r.close();
			 pw.close();
			 
			BufferedReader reader = new BufferedReader(new FileReader("blahML.html"));	
			String s;
			
			String link = "";
			String requester="";
			
			String title = "";
			String requesterURL="";
			String requesterID = "";
			
			String reward = "";
			
			boolean newLink = false;
			
			
			while((s = reader.readLine()) != null)
			{	
				s = s.trim();
				boolean hit = false;
			//	System.out.println(s);
				if(s.startsWith("<div class=\"left\">")) // hit link
				{
					s = filterWebURL(s);
					s  = s.substring(s.indexOf("https://"));
					link  = s.substring(0, s.indexOf("\""));
					
					s = s.substring(s.indexOf("title="));
					s = s.substring(7);
					title = s.substring(0, s.indexOf("\""));
		
					
				}
				if(s.startsWith("<div class=\"right\">")) // requester link
				{
				//	System.out.println(filterWebURL(s));
					
					s = filterWebURL(s);
					s  = s.substring(s.indexOf("https://"));
					requesterURL = s.substring(0, s.indexOf("\""));
					
					
					s = s.substring(s.indexOf("title="));
					s = s.substring(7);
					requester = s.substring(0, s.indexOf("\""));

					requesterID = requesterURL.substring(requesterURL.indexOf("Id=") + 3);
				
				
					
				}
				if(s.startsWith("<div class=\"money\">")) // requester link
				{
					
					s = s.substring(s.indexOf("[Pay:"));					
					reward = s.substring(s.indexOf("$"), s.indexOf("]"));
					
				}
				
				//by the time I get into this if, I should have all my data already.
				if(s.startsWith("<button class=\"deadButt\"") || s.startsWith("<button class=\"aliveButt\""))
				{
					if(s.startsWith("<button class=\"aliveButt\"")) // if there is an alive button, then this means the hit has been marked dead
					{
						hit = false;
						
						//reset variable
						link = "";
						requester="";
						title = "";
						requesterURL="";
						requesterID = "";
						reward = "";
						
					}
					else// shows dead button, meaning it's still alive
					{
						
						hit =true;
					}

				}
				
				//will go in here if we already have all the URL ready.
				if(hit)
				{
					String PandA = "";
					if(link.toLowerCase().contains("previewandaccept")) // it's a PandA link
					{
						PandA = link;
					}
					else // it's just a regular preview... link. Let's converted it to previewand accept
					{
						PandA = link.replaceAll("preview", "previewandaccept");
					}		
					
					/*
					System.out.println(title);
					System.out.println(link);
					System.out.println(requester);
					System.out.println(requesterURL);
					System.out.println(requesterID);
					System.out.println(PandA);
					System.out.println(reward);
					*/
					
					newLink = checkIfLinkExist(createExportHit(title, link, requester, requesterURL, requesterID, PandA, reward), PandA, "ML",3600000, alJson);
					
					
					link = "";
					requester="";
					title = "";
					requesterURL="";
					requesterID = "";
					reward = "";
					
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
				
				newLink = false;
				
			}
				
			System.out.println(new Date() + " <<MTURK LIST>> Finished Mturk List");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public ArrayList<String> createExportHit(String title, String link, String requester, String requesterURL, String requesterID, String Panda, String reward){
		ArrayList<String> text = new ArrayList<String>();
	
			String TOURL = "https://turkopticon.ucsd.edu/api/multi-attrs.php?ids=";
			String imgURL = "";
			try
			{
				 imgURL = getTOImage(TOURL, requesterID);
			}
			catch(Exception e) // might error if there is no TO JSON data for this requester
			{
				System.out.println(e.getMessage());
				
				imgURL = "";
			}
			
			String temp = "";
			//lets build our string
			temp = "<b>Title:</b> <a href=\" " + link + " \" target=\"_blank\">";	
			text.add(temp);
			
			temp = "<font color=\"blue\">" + title +" </font></a><br>";
			text.add(temp);
			
			temp = "<b>Requester:</b> <a href=\""+ requesterURL +"\" target=\"_blank\"> <font color=\"blue\">" + requester+ "</font></a> "+ requesterID;
			text.add(temp);
			
			temp = "(<a href=\"http://turkopticon.ucsd.edu/"+ requesterID +"\" target=\"_blank\"><font color=\"blue\">TO</font></a>)";
			text.add(temp);
			
			temp="<br><b>TO Ratings:</b><br>";
			text.add(temp);
			
			text.add(imgURL);
			
			temp = "<br> <b>Reward:</b> <font color=\"green\"><b>" +reward + "</b></font><br>";
			text.add(temp);

		return text;
		
	}
	
	public String getTOImage(String url, String requesterID) throws Exception 
	{
		
			JSONObject json = readJsonFromUrl(url + requesterID);
		 
		    JSONObject jsonReq  = (JSONObject) json.get(requesterID);
		    JSONObject jsonReqAttrs = (JSONObject) jsonReq.get("attrs");
		    
			
		    String imgURL = "http://data.istrack.in/turkopticon.php?data="+ jsonReqAttrs.get("comm")+"," + jsonReqAttrs.get("pay")+ "," + jsonReqAttrs.get("fair")+","+jsonReqAttrs.get("fast"); 
		    
			
		     imgURL = "<img src=\" " + imgURL + "\" > </img> <br>Number of Reviews: " + jsonReq.get("reviews") +"";
		    		     
		    return imgURL;    	    
		    
	}
	
	 private static String readAll(Reader rd) throws IOException {
		    StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = rd.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		  }

		  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		    InputStream is = new URL(url).openStream();
		    try {
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		      String jsonText = readAll(rd);
		      JSONObject json = new JSONObject(jsonText);
		      return json;
		    } finally {
		      is.close();
		    }
	  }

		  
		  
	public void turkerNation() throws Exception
	{
		try
		{
			System.out.println(new Date() + " <<FORUM>> Started Turker Nation");
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
			System.out.println(new Date() + " <<FORUM>> Finished Turker Nation");
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
						newLink = checkIfLinkExist(text, PandA, "TN",3600000, alJson);
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
		File newFile = new File("blahTN2.html");
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
		System.out.println(new Date() + " <<FORUM>> Started Mturk Grind");
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
		System.out.println(new Date() + " <<FORUM>> Finished Mturk Grind");
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
						newLink = checkIfLinkExist(text, PandA, "MTG", 3600000, alJson);
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
		File newFile = new File("blahMG2.html");
		if(newFile.exists()&& (!test))
		{
			newFile.delete();

		}

	}
	
	public String filterWebURL(String s)
	{
		s = s.replaceAll("%3A", ":");
		s = s.replaceAll("%2F", "/");
		s = s.replaceAll("%3F", "?");
		s = s.replaceAll("%3D", "=");
		s = s.replaceAll("%26", "&");
		
		
		return s;
		
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
		System.out.println(new Date() + " <<REDDIT>> Started Reddit HWTF");
		timer.runHWTF = false;
		try{
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
				    		 
				    		 //if 2 below this contain dead then it's dead, we dont add
				    		 st.nextToken().trim();
				    		 String tempStr = st.nextToken().trim().toLowerCase();
				    		 if(!tempStr.contains("dead"))
				    		 {
				    			 list.add("http://www.reddit.com"+test);				    			 
				    		 }
				    		 				    		
				    	 }
				     }
				}
				
			}
			
			
			boolean newLink = false;
			//list.clear();
			//list.add("https://www.reddit.com/r/HITsWorthTurkingFor/comments/3eyuj7/us_judging_various_events_nazli_turan_050325_94/");
			
			//we now have our list array with all the thread links.
			for(int i=0; i< list.size(); i++)
			{
				
				url = list.get(i);
				pageURL = new URL(url); 
				urlConnection = (HttpURLConnection) pageURL.openConnection();
				urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();
								in = new BufferedInputStream(urlConnection.getInputStream()); 
				pw = new PrintWriter(new FileWriter("blah2HWTF.html"));
				r = new InputStreamReader(in);
			
				 while((c = r.read()) != -1) 
				 {         	
				     pw.print(String.valueOf((char)c)); 
				 } 
				   r.close();
				   pw.close();
				
				 reader = new BufferedReader(new FileReader("blah2HWTF.html"));	
				 boolean llbreak = false;
				 while((s = reader.readLine()) != null || llbreak)
				 {	
					s = s.trim();
					if(s.startsWith("</div></form><div class=\"bottom\">"))
					{
						StringTokenizer st = new StringTokenizer(s,"<");
						String PandA="";
						ArrayList<String> text= new ArrayList<String>();
					     while (st.hasMoreTokens()) 
					     {
					    	 String test = st.nextToken();
					    	 test = test.trim();
					    	 
					    	 if(test.startsWith("a class=\"title")) // get the thread name
					    	 {					    		
					    		 test = test.substring(test.indexOf(">")); 
					    		 text.add( test + "</br></br></br></br></br></br></br>");
					    		// System.out.println(new Date() + " "+ text.get(0));
					    		 
					    	 }

					    	 if(test.startsWith("a href=\"https://www.mturk.com"))
					    	 {
					    		 
					    		 String temp =  "<"+test+"</a>";
					    		 //Need to add open to new tab
					    		 temp = temp.substring(0,3) + "target=_blank " + temp.substring(3);					    		
					    		 
					    		 text.set(0, text.get(0)+ temp); // lets add to the post
					    		 
					    		 test = test.substring(test.indexOf("href=\"")); //trim the crap before href
					    		 test = test.substring(test.indexOf("href=\""), test.indexOf("\"", 50)); // 50 to insure we hit the " we want
					    		 test = test.replace("href=\"", "");
					    		 test = test.replace("&amp;", "&");
					    		 			
					    		 
					    		 
					    		 //createExportHitLink();
					    			//could potentially be previewandaccept
								if(test.contains("https://www.mturk.com/mturk/preview"))
								{
									if(test.toLowerCase().contains("previewandaccept")) // it's a PandA link
									{
										PandA = test;
									}
									else // it's just a regular preview... link. Let's converted it to previewand accept
									{
										PandA = test.replaceAll("preview", "previewandaccept");
									}
								}
								else // some kind of other mturk link, for reddint search bar for requester name is very common.
								{
									PandA = test;
								}
								
								// we gotta match the link with our records to see if we've sent it before
								newLink = checkIfLinkExist(text, PandA, "HWTF", 3600000, alJson);												    	
					    		 
								System.out.println(new Date() + " <<REDDIT>> "+ PandA);
								
					    		 llbreak = true; //break out
					    		 break;
					    	 }
					    	 
					     }
								
					}
					
					if(llbreak)
					{
						break;
					}
					 
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
			
			
			//Reddit scrapes take too long... if there are too many new hits it takes more then a min to finish
			// have to thread this method.
			/*
			if(alJsonReddit.size()>0)
			{
				System.out.println(new Date() + " New REDDIT HITS writing to JSON");
				
				// I have to hope that main loop's writeToJSON already finished and FTP.
				// chances are it will, then there won't be overlap
				writeToJSON(alJsonReddit);
				alJsonReddit.clear();
			}
			*/
			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	//	timer.runHWTF = true;
		System.out.println(new Date() + " <<REDDIT>> Finished Reddit HWTF");
			
		
	}
	
	public void createExportHitLink(String u) throws Exception
	{	
		if(u.startsWith("https://www.mturk.com/mturk/searchbar"))
		{
			getSearchBarHit( u);
		}
		
		
		
	}
	
	public void getSearchBarHit(String u) throws Exception
	{
		String url = u;
				//String url = "https://www.mturk.com/mturk/searchbar?selectedSearchType=hitgroups&requesterId=A2MY681P424NK";
				URL pageURL = new URL(url); 
				HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
				urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();
				

				String fileName = "temp"+UUID.randomUUID().toString()+".html";
				
				
				InputStream in = new BufferedInputStream(urlConnection.getInputStream()); 
				PrintWriter pw = new PrintWriter(new FileWriter(fileName));
				
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
					
				BufferedReader reader = new BufferedReader(new FileReader(fileName));	
				String s;
				while((s = reader.readLine()) != null)
				{
					System.out.println(s);
				}
				
				reader.close();
				
				
				
				File f= new File(fileName);
				if(f.exists())
				{
					f.delete();
				}
	}
	public void TurkForum() throws Exception
	{		
		try{
		System.out.println(new Date() + " <<FORUM>> Started Mturk Forum");
		String todayLink = getTodayLink("http://mturkforum.com/forumdisplay.php?30-Great-HITS", true);
		todayLink = window.getInstance().getURL().equals("") ? todayLink : window.getInstance().getURL();
	//	todayLink =  "showthread.php?13640-Can-t-Find-FUN-HIT-s-01-30-Super-Funbowl-Friday!!";
		if(!todayLink.equals(""))
		{
			//processPage("http://mturkforum.com/showthread.php?33612-Can-t-find-Rick-tastic-HITS-7-27-Morty-fying-Monday!/page79");
			processPage("http://mturkforum.com/"+todayLink+"/page1000"); //1000 so its greater so it's always the last page
			
		}
		else
		{
			//System.out.println("error grabbing today's thread");
			window.getInstance().addText("error grabbing today's thread");
		}
		
		window.getInstance().setLblTime();
		System.out.println(new Date() + " <<FORUM>> Finished Mturk Forum");
		
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
						newLink = checkIfLinkExist(text, PandA, "MTF", 3600000, alJson);
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
	// specify which json array list to add hits
	public boolean checkIfLinkExist(ArrayList<String> a, String l, String source, int time , ArrayList<String> jsonList) throws Exception
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
			hitData hd = new hitData(l, new Date(), source, time);
			myData.getArray().add(hd);
			
			read.seralize(myData); //Write back data class
			
			//write to full data
			addSeralize(readFull,hd);
			
			
			writeToJSONPerHIT(a,l, jsonList);
					
			newLink = true;
			
			
			System.out.println(new Date() + " FOUND NEW HIT FROM " + source);
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
	
	
	public void writeToJSON(ArrayList<String> jsonList)
	{
		String start = "{\"records\":[";
		//String end = "]}";
		String end = "], \"date\": \""+ new Date() +"\"}";
		String str= "";
		
		for(int i=0; i < jsonList.size(); i++)
		{
			str += jsonList.get(i) + ",";
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
			
			System.out.println(new Date() + " <<FTP>> Starting FTP...");
			if(!test)
			{
				FTP(jsonFile,"public_html");
			}
			System.out.println(new Date() + " <<FTP>> Finished FTP...");
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
	public void writeToJSONPerHIT(ArrayList<String> a, String l, ArrayList<String> jsonList)
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
		jsonList.add(str);
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

