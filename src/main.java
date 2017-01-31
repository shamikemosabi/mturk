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

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.text.*;

import org.apache.commons.net.ftp.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import com.google.firebase.*;
//import com.google.firebase.database.*;


public class main extends TimerTask 
{
	data myData ;
	readData read;
	
	data liveData = new data();
	
	readData readLive; // I guess I'm using this as "live hits"
	
	static window w;
	
	boolean test = false;
	
	int timeToCheckExceed = 0;
	
	String jsonFile = "C:\\inetpub\\wwwroot\\www3\\test.aspx";
	String jsonFileLive = "C:\\inetpub\\wwwroot\\www3\\testLive.aspx";
	ArrayList<String> alJson = new ArrayList<String>();
	
	// specifically for HWTF hits
	ArrayList<String> alJsonReddit = new ArrayList<String>();
	
	boolean VPN = false;
	log logger = null;
	
	public main() throws Exception
	{

	
		w = window.getInstance();
			
		String d = (test)? "dataTEST.ser": "data.ser";
		read = new readData(d); //object used to seralize and deseralize
		readLive = new readData("dataLIVE.ser");
		logger = new log();
	}	
	
	/**
	 * Remove hits that's been over 1 hr.
	 */
	public void cleanHit() throws Exception
	{
		myData  = read.deSeralize();
		Date currDate = new Date();
		ArrayList remIndex = new ArrayList();
		
		logger.info("<<FTP>> cleaning hits");
		for(int i=0; i< myData.getArray().size(); i++)
		{
			hitData hd = myData.getArray().get(i);
			Date hitDate = hd.getDate();
			//I can replace 3600000 with hitDate.getTimeExpire()
			if(currDate.getTime() - hitDate.getTime() >= 3600000) // 60 mins
			{				
				remIndex.add(i);				
			}	
		}

		// loop reverse, because ArrayList remove will shift index
		for(int j = remIndex.size() - 1; j >= 0; j--){			 
			Integer i = (Integer) remIndex.get(j);
			myData.getArray().remove(i.intValue());
			
			logger.info("<<FTP>> Removing hits");
			
		}
	
		read.seralize(myData); //Write back data class
	}
	
	public void doMturkList()
	{
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	 
		    	 while(true)
	    		 {
		    	 	try{
		     
		    	 		logger.info("<<MTURK LIST>> STARTED");
		    				
		    			mturkList();
		    			
		    			logger.info("<<MTURK LIST>> FINISHED");	
		    			Thread.sleep(timer.timeInterval(5,10)); //FTP every minute		    				
		    		 }
		    	 	 catch(Exception e)
			    	 {
		    	 		logger.info(e.getMessage());
			    		 e.printStackTrace();
			    	 }		    		 
		    	 }
		    	
		     }
		});  
		t1.start();
		
		
	}

	public void doWriteFTP()
	{
		
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	 
		    	 while(true)
	    		 {		    	 
		    		 try{
		    			 logger.info("<<FTP>> DOWRITEFTP STARTED");
		    		 				
		    				if(alJson.size()>0)
		    				{
		    					logger.info("<<FTP>> New hits writing to JSON");
		    					writeToJSON(alJson, jsonFile );
		    					//alJson.clear();
		    					logger.info("<<FTP>> Cleared alJSON");
		    				}
		    				
		    				cleanHit();
		    				logger.info("<<FTP>> DOWRITEFTP FINISHED");	
		    				Thread.sleep(5000); //FTP 10 secs!
	    				
		    		 }
		    		 catch(Exception e)
			    	 {
		    			 logger.info(e.getMessage());
			    		 e.printStackTrace();
			    	 }
		    		 
		    	 }
		    	 
		     }
		});  
		t1.start();
		
		

	}
	
	// Threaded each forum
	/*
	public void doForum()
	{
		
		 //reddit scraps take too long, have to thread it.
		 
		
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	
		    	 while(true)
	    		 {
		    	   try{

		    		   	logger.info("<<FORUM>> STARTED");	
		    		   	 turkerHub();
		    			 mturkGrind();
		    			 TurkForum();
		    			 mturkCrowd(); 		    			 
		    			 logger.info("<<FORUM>> FINISHED");
			    		 Thread.sleep(timer.timeInterval());	
		    		 }
		    	   catch(Exception e)
			    	 {
		    		   logger.info(e.getMessage());
			    		 e.printStackTrace();
			    	 }
		    		 
		    	 }
		    	 
		     }
		});  
		t1.start();
		
	}
*/
	
	public void doTurkerHub()
	{
		/*
		 * reddit scraps take too long, have to thread it.
		 */		
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	
		    	 while(true)
	    		 {
		    	   try{		    		   
		    		   	 turkerHub();		    			
			    		 Thread.sleep(timer.timeIntervalLimit());	
		    		 }
		    	   catch(Exception e)
			    	 {
		    		   logger.info(e.getMessage());
			    		 e.printStackTrace();
			    	 }
		    		 
		    	 }
		    	 
		     }
		});  
		t1.start();		
	}
	
	public void doMturkGrind()
	{
		/*
		 * reddit scraps take too long, have to thread it.
		 */		
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	
		    	 while(true)
	    		 {
		    	   try{	
		    		   	 mturkGrind();
			    		 Thread.sleep(timer.timeInterval());	
		    		 }
		    	   catch(Exception e)
			    	 {
		    		   logger.info(e.getMessage());
			    		 e.printStackTrace();
			    	 }
		    		 
		    	 }
		    	 
		     }
		});  
		t1.start();		
	}
	
	public void doMturkForum()
	{
		/*
		 * reddit scraps take too long, have to thread it.
		 */		
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	
		    	 while(true)
	    		 {
		    	   try{	
		    		   	 TurkForum();
			    		 Thread.sleep(timer.timeInterval());	
		    		 }
		    	   catch(Exception e)
			    	 {
		    		   logger.info(e.getMessage());
			    		 e.printStackTrace();
			    	 }
		    		 
		    	 }
		    	 
		     }
		});  
		t1.start();		
	}
	
	public void doMturkCrowd()
	{
		/*
		 * reddit scraps take too long, have to thread it.
		 */		
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	
		    	 while(true)
	    		 {
		    	   try{	
		    		   	 mturkCrowd();
			    		 Thread.sleep(timer.timeInterval());	
		    		 }
		    	   catch(Exception e)
			    	 {
		    		   logger.info(e.getMessage());
			    		 e.printStackTrace();
			    	 }
		    		 
		    	 }
		    	 
		     }
		});  
		t1.start();		
	}
	
	
	public void turkerHub() throws Exception
	{
		try
		{
			logger.info("<<Turker Hub>> Started Turker Hub");
			String todayLink = getTodayLinkTHsoup("https://turkerhub.com/forums/daily-threads.2/", true);
			
			if(!todayLink.equals(""))
			{
				processPageTH("http://turkerhub.com/"+todayLink+"/page-1000"); //1000 so its greater so it's always the last page
				//lets not use soup. with soup I don't have the actual tables posted by forum users
//				processPageTHsoup("http://turkerhub.com/"+todayLink+"/page-1000"); //1000 so its greater so it's always the last page		
			}
			else
			{
				//System.out.println("error grabbing today's thread");
				window.getInstance().addText("error grabbing TH thread");
			}
			
			window.getInstance().setLblTime();
			logger.info("<<Turker Hub>> Finished Turker Hub");
		}
		catch(Exception e)
		{
			logger.info(e.getMessage());
		}
		
	}
	
	public void processPageTH(String u) throws Exception
	{
		String url = u;
		//
		
		URL pageURL = new URL(url); 
		HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
		urlConnection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
		urlConnection.addRequestProperty("User-Agent", "Mozilla");
		urlConnection.addRequestProperty("Referer", "google.com");
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();
		
		if(urlConnection.getResponseCode() == 301){ //wtf reddit redirecting me, but to the same URL
			String newUrl = urlConnection.getHeaderField("Location");
			urlConnection  = (HttpURLConnection) new URL(newUrl).openConnection();
			urlConnection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			urlConnection.addRequestProperty("User-Agent", "Mozilla");
			urlConnection.addRequestProperty("Referer", "google.com");
			urlConnection.setRequestMethod("GET");							
			urlConnection.connect();
		}
		
		
		InputStream in = new BufferedInputStream(urlConnection.getInputStream()); 
		PrintWriter pw = new PrintWriter(new FileWriter("blahTH2.html"));
		
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
		
			
		BufferedReader reader = new BufferedReader(new FileReader("blahTH2.html"));	
		String s;
		while((s = reader.readLine()) != null)
		{
			String temp = s.toLowerCase().trim(); //let's trim it first

			if(temp.startsWith("<blockquote class="))
			{
				boolean hit = false;
				ArrayList<String> text = new ArrayList<String>();
				CEDTEXT cedtext = null;
				String hitLink = "";
				String PandA ="";
				boolean hasTable = false;
				ArrayList<String> textTable = new ArrayList<String>();
				
				while(!temp.equals("</blockquote>")) // keep looping all the text the poster did store them
				{
					temp =reader.readLine().trim();
					
					if(temp.contains("<table class=\""))
					{						
						hasTable=true;
					}
					if(temp.contains("</table>")) // we hit the end of table, textTable should contain all the strings within table
					{
						hasTable=false;
												
						//mturk grind forum have </table> on the same line, I need to extract what's before </table>
						String t = temp.substring(0, temp.lastIndexOf("</table>")+ 8);
						
						if(t.contains("mturksuite.com"))
						{
							String replaceStr= t.substring(t.indexOf("<div style=\"text-align: center\">"), t.indexOf("</table>"));
							t = t.replace(replaceStr, "");
						}
						textTable.add(t);
						
					}
					
					if(hasTable)
					{
						if(filterPost(temp))
						{
							textTable.add(temp);
						}
					}
					
					if(temp.contains("<a href=\"https://www.mturk.com/mturk/preview") || temp.contains("<a href=\"https://www.mturk.com/mturk/accept") || temp.contains("<a href=\"https://www.mturk.com/mturk/searchbar") ) //we found a mturk link posible hit!
					{
						hit = true;	
						//gotta clean up the string
						
						String temp2 = "";
						temp2 = temp.replace("&amp;", "&");
										
						temp2 = temp2.substring(temp.indexOf("<a href=\"https:")); // lets trim the crap before <a href
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
				
				if(hit)
				{
					cedtext = filterSmartMode(1, textTable, hitLink);
					hit = (cedtext==null)? false: true;
				}
				
				// if hit = true then we have to do more stuff, if not we keep looping
				if(hit)
				{
					// we gotta match the link with our records to see if we've sent it before
						newLink = checkIfLinkExist(cedtext.getTextTable(), PandA, "TH", 10800000, alJson,cedtext.getCED());
				}
				
				//reset hit and text	
				// wait.. doesn't these 2 get reset 
			//	hit = false;
				//text.clear();
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
		File newFile = new File("blahTH2.html");
		if(newFile.exists()&& (!test))
		{
			newFile.delete();

		}

	}
	
	public void processPageTHsoup(String u) throws Exception
	{
		
		Document doc = Jsoup.connect(u).userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3").get();
				
		Elements links = doc.select("a[href]");
		
		 for (Element b : links)
		 {
	
				boolean hit = false;
				ArrayList text = new ArrayList();
				CEDTEXT cedtext = null;
				String hitLink = "";
				String PandA ="";
				String temp = b.toString();
			 
			
			 if(temp.contains("<a href=\"https://www.mturk.com/mturk/preview") || temp.contains("<a href=\"https://www.mturk.com/mturk/accept") || temp.contains("<a href=\"https://www.mturk.com/mturk/searchbar") ) //we found a mturk link posible hit!
			 {
					hit = true;	
					//gotta clean up the string
					temp = b.attr("abs:href"); 
					
					String temp2 = "";
					temp2 = temp.replace("&amp;", "&");
								
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
			 
			 
				//coming out, if my PandA is still empty, then that means there was no preview link, It's either an "accept" link or a "searchbar" link.
				// let's just assign that hitlink to PandA.
				if(hit && PandA.equals(""))
				{
					PandA = hitLink;
				}
				if(hit)
				{
					cedtext = filterSmartMode(1, new ArrayList<String>(), hitLink);
					hit = (cedtext==null)? false: true;
				}
				if(hit)
				{
					
					// we gotta match the link with our records to see if we've sent it before
				     checkIfLinkExist(cedtext.getTextTable(), PandA, "TH",10800000, alJson, cedtext.getCED()); 
				}
			 
							 
	       }
		
		
	}
	
	public String getTodayLinkTHsoup(String u, boolean b) throws Exception
	{
				
			Document doc = Jsoup.connect(u).userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3").get();					
			Elements link  = doc.getElementsByClass("PreviewTooltip");
			
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
		    
		    		    			   
			 for (Element e : link) {
				 String linkText = e.text(); //e.attr("data-previewUrl"); // 
				 
			
				 
				 
				 if( linkText.toLowerCase().contains(dateFormat.format(d.getTime()))
				    		||linkText.toLowerCase().contains(dateFormat2.format(d.getTime()))
				    		||linkText.toLowerCase().contains(dateFormat3.format(d.getTime()))
				    		) //compare if it's the same
				 {			
					String s = e.attr("data-previewUrl"); //get preview link														
			    	ret = s.replaceAll("/preview", "");
			    	break;
				 }
				 
		       }
			 

				if(!b && ret.equals(""))
				{
					return ret;
				}

				if(ret.equals(""))
				{
					ret = getTodayLinkTNsoup(u, false);
				}
	
				return ret;
	}
	
	
	public void doTurkerNation()
	{
		/*
		 * reddit scraps take too long, have to thread it.
		 */
		
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	
		    	 while(true)
	    		 {
		    	   try{		    			    		
		    			 turkerNation();
			    	
			    		 Thread.sleep(timer.timeIntervalLimit());	
		    		 }
		    	   catch(Exception e)
			    	 {
		    		   logger.info(e.getMessage());
			    		 e.printStackTrace();
			    	 }
		    		 
		    	 }
		    	 
		     }
		});  
		t1.start();
		
	}
	
	
	
	
	
	public boolean checkExceedVPN(String s) throws Exception
	 {
		if(!s.contains("DOC") || VPN)
		{
			return true;

		}
		
		VPN = true;
		
		 String host = "smtp.gmail.com";
		    String from = "docogo1@gmail.com";
		    String pass = "vjvviogjtthyttxa";
		    Properties props = System.getProperties();
		  		    
		    props.put("mail.smtp.starttls.enable", "true"); // added this line
		    props.put("mail.smtp.host", host);
		    props.put("mail.smtp.user", from);
		    props.put("mail.smtp.password", pass);
		    props.put("mail.smtp.port", "587");
		    props.put("mail.smtp.auth", "true");	
		    
		    
		    //String[] to = {"shamikemosabi@gmail.com"}; // added this line
		    String[] to = {"6462841208@tmomail.net"}; // added this line
		    
		   
		    Session session = Session.getDefaultInstance(props, new GMailAuthenticator(from, pass));
		    MimeMessage message = new MimeMessage(session);
		    message.setFrom(new InternetAddress(from));
		    
		    
		  
			    InternetAddress[] toAddress = new InternetAddress[to.length];
			    
			    for( int i=0; i < to.length; i++ ) { // changed from a while loop
			        toAddress[i] = new InternetAddress(to[i]);
			    }
			    
			    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
			        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			    }
		   
		   
		    
		    message.setSubject("VPN exceeded");
		   //message.setContent(body, "text/html");
		    message.setContent("VPN exceeded", "text/plain");
		    Transport transport = session.getTransport("smtp");
		    transport.connect(host, from, pass);
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
		    
		    return true;
	     
	 }
	
	
	
	public void doRedditHWTF()
	{
		/*
		 * reddit scraps take too long, have to thread it.
		 */
		
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	
		    	 while(true)
	    		 {
		    		 try{
		    			 
		    				URL url = new URL("http://checkip.amazonaws.com/");
		    				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		    				String temp  = br.readLine();
		    				logger.info(temp);
		    				
		    				checkExceedVPN(temp);
		    					    				
		    				br.close();
		    			 
		    			 logger.info("<<REDDIT>> STARTED");		
			    		 RedditHWTF();
			    		 logger.info("<<REDDIT>> FINISHED");	 
			    		 Thread.sleep(60000);
			    		 		    					    		
		    		 }
		    		 catch(Exception e)
			    	 {
		    			 logger.info(e.getMessage());
			    		 e.printStackTrace();
			    	 }		    		 		    		
		    		 
		    	 }
		    	
		     }
		});  
		t1.start();
		
	}
	
	public void mturkList() throws Exception
	{
		try{
			logger.info("<<MTURK LIST>> Started Mturk List");
				
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
			
			String time = "";
			
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
	
				if(s.startsWith("<div class=\"time\" title=\"Average completion time\">")) // time
				{
					s = s.substring(s.indexOf("Time:") + 5);
					time = s.substring(0, s.indexOf("<"));				
					
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
						time = "";
						
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
					
					
					CEDTEXT cedtext = filterSmartMode(3, new ArrayList<String>(), PandA);
					hit = (cedtext==null)? false: true;
					
					if(hit)
					{
						 checkIfLinkExist(cedtext.getTextTable(), PandA, "ML",10800000, alJson, cedtext.getCED()); 			
						//newLink = checkIfLinkExist(createExportHit(title, link, requester, requesterURL, requesterID, PandA, reward, time), PandA, "ML",10800000, alJson, new createExportData());
					}
					
					
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
				
			logger.info("<<MTURK LIST>> Finished Mturk List");
		}
		catch(Exception e)
		{
			logger.info(e.getMessage());
		}
	}
	
	
	/*
	 * overload method,
	 * 
	 * make it more general, I can have some fields that are empty
	 * so I dont want export it to show.
	 */
	public ArrayList<String> createExportHit(createExportData CED, ArrayList<String> a){
		ArrayList<String> text = new ArrayList<String>();
		String TOURL = "https://turkopticon.ucsd.edu/api/multi-attrs.php?ids=";
		String imgURL = "";
		
		if(!CED.getRequesterID().equals(""))
		{
			try{
				imgURL = getTOImage(TOURL, CED.getRequesterID());
			}
			catch(Exception e) // might error if there is no TO JSON data for this requester
			{
				logger.info(e.getMessage());
				
				imgURL = "";
			}
		}
		
		String temp = "";
		String t1="";
		String t2="";
				
		if(!CED.getLink().equals(""))
		{
			temp = "<a href=\" " + CED.getLink() + " \" target=\"_blank\">";	
			t1 = temp;
			//text.add(temp);
		}
		
		if(!CED.getTitle().equals(""))
		{
			temp = "<font color=\"blue\"> " + CED.getTitle() +" </font>";
			t2 = temp;
			//text.add(temp);
		}					
		// I can have link but no title, and vice versa. 
		// This could be a problem I want to display the Title only 
		// but it doesn't make sense, if I only have the link, what do I want to display?
		text.add(((!CED.getTitle().equals("")) ? "<b>Title:</b>": "") + t1 + t2 +((!CED.getLink().equals("")) ? "</a>":"") + "</br>" );

		
		//reset it
		t1="";
		t2="";
		
		if(!CED.getRequesterURL().equals(""))
		{
			temp = "<a href=\""+ CED.getRequesterURL() +"\" target=\"_blank\">";	
			t1 = temp;
			//text.add(temp);
		}
		
		if(!CED.getRequester().equals(""))
		{
			temp = "<font color=\"blue\"> " + CED.getRequester() + "</font>"+ (CED.getRequesterID().equals("")?"": "["+CED.getRequesterID()+"]");
			t2 = temp;
			//text.add(temp);
		}
		//same logic as title, I can have requester URL ID with out requester and vice versa
		text.add(((!CED.getRequester().equals("")) ? "<b>Requester:</b>": "") + t1 + t2 +((!CED.getRequesterURL().equals("")) ? "</a>":"") + "</br>" );
		
		if(!CED.getRequesterID().equals(""))
		{
			temp = "(<a href=\"http://turkopticon.ucsd.edu/"+ CED.getRequesterID()  +"\" target=\"_blank\"><font color=\"blue\">TO</font></a>)";
			text.add(temp);
			
			// if I have requester ID I will also have TO Rating, imgURL.
			temp="<br><b>TO Ratings:</b><br>";
			text.add(temp);
			text.add(imgURL);
			
		}
		
		if(!CED.getDesc().equals(""))
		{
			temp="</br> <b> Description: </b>" + CED.getDesc()  ;
			text.add(temp);
		}
			
			
		if(!CED.getTime().equals(""))
		{
			
			temp = "</br> <b>Time:</b> " + CED.getTime();
			text.add(temp);
		}
		
		if(!CED.getReward().equals(""))
		{
			temp = "<br> <b>Reward:</b> <font color=\"green\"><b>" + CED.getReward() + "</b></font>";
			text.add(temp);
			
		}
		if(!CED.getQual().equals(""))
		{
			temp="</br> <b> Qualifications: </b>" + CED.getQual()  ;
			text.add(temp);
		}
			
		
		if(!CED.foundHit)
		{
			
		}
		else // if I did find a hit, just use text
		{
			
			a = text;
		}
		
		return  a;
		
	}
	public ArrayList<String> createExportHit(String title, String link, String requester, String requesterURL, String requesterID, String Panda, String reward, String time){
		ArrayList<String> text = new ArrayList<String>();
	
			String TOURL = "https://turkopticon.ucsd.edu/api/multi-attrs.php?ids=";
			String imgURL = "";
			try
			{
				 imgURL = getTOImage(TOURL, requesterID);
			}
			catch(Exception e) // might error if there is no TO JSON data for this requester
			{
				logger.info(e.getMessage());
				
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
			
			temp = "<br>" + time + "</br>";
			text.add(temp);

		return text;
		
	}
	
	public String getTOImage(String url, String requesterID) throws Exception 
	{
		
			JSONObject json = readJsonFromUrl(url + requesterID);
		 
		    JSONObject jsonReq  = (JSONObject) json.get(requesterID);
		    JSONObject jsonReqAttrs = (JSONObject) jsonReq.get("attrs");
		    
			
		    String imgURL = "http://data.istrack.in/turkopticon.php?data="+ jsonReqAttrs.get("comm")+"," + jsonReqAttrs.get("pay")+ "," + jsonReqAttrs.get("fair")+","+jsonReqAttrs.get("fast"); 
		    
			
		     imgURL = "<img src=\" " + imgURL + "\" > </img> <br><b>Number of Reviews: </b>" + jsonReq.get("reviews") +"";
		    		     
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
			logger.info("<<FORUM>> Started Turker Nation");
			String todayLink = getTodayLinkTNsoup("http://turkernation.com/forumdisplay.php?157-Daily-HIT-Threads", true);
			
			if(!todayLink.equals(""))
			{
				//processPageTN("http://turkernation.com/showthread.php?25209-08-12-15-wicked-wednesday/page15");
				processPageTNsoup("http://turkernation.com/"+todayLink+"/page1000"); //1000 so its greater so it's always the last page
			//	processPageTNsoup("http://turkernation.com/showthread.php?27139-06-05-16-Cry-Havoc-And-Let-Slip-The-PandAs-Of-Mturk-Sunday/page17");
				
				//processPageTNsoup("http://turkernation.com/showthread.php?27169-Tasty-HITs-Thursday/" + "page1000");
			}
			else
			{
				//System.out.println("error grabbing today's thread");
				window.getInstance().addText("error grabbing TN thread");
			}
			
			window.getInstance().setLblTime();
			logger.info("<<FORUM>> Finished Turker Nation");
		}
		catch(Exception e)
		{
			logger.info(e.getMessage());
		}
		
	}
	
	public void processPageTNsoup(String u) throws Exception
	{
		
		Document doc = Jsoup.connect(u).userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3").get();
		
		
		Elements links = doc.select("a[href]");
		
		 for (Element b : links)
		 {
	
				boolean hit = false;
				ArrayList text = new ArrayList();
				CEDTEXT cedtext = null;
				String hitLink = "";
				String PandA ="";
			 String temp = b.toString();
			 
			
			 if(temp.contains("<a href=\"https://www.mturk.com/mturk/preview") || temp.contains("<a href=\"https://www.mturk.com/mturk/accept") || temp.contains("<a href=\"https://www.mturk.com/mturk/searchbar") ) //we found a mturk link posible hit!
			 {
					hit = true;	
					//gotta clean up the string
					temp = b.attr("abs:href"); 
					
					String temp2 = "";
					temp2 = temp.replace("&amp;", "&");
								
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
			 
			 
				//coming out, if my PandA is still empty, then that means there was no preview link, It's either an "accept" link or a "searchbar" link.
				// let's just assign that hitlink to PandA.
				if(hit && PandA.equals(""))
				{
					PandA = hitLink;
				}
				if(hit)
				{
					cedtext = filterSmartMode(3, new ArrayList<String>(), hitLink);
					hit = (cedtext==null)? false: true;
				}
				if(hit)
				{
					
					// we gotta match the link with our records to see if we've sent it before
				     checkIfLinkExist(cedtext.getTextTable(), PandA, "TN",10800000, alJson, cedtext.getCED()); 
				}
			 
							 
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
				CEDTEXT cedtext= null;
				String hitLink = "";
				String PandA ="";
				
				boolean hasTable = false; 
				boolean firstTable = true; // only get the first table
				ArrayList<String> textTable = new ArrayList<String>();
				
				while(!temp.equals("</blockquote>")) // keep looping all the text the poster did store them
				{
					temp =reader.readLine().trim();
					
					// if we are in a post, and there is a table, chances are it's a hit
					if(temp.contains("<table class=\""))
					{						
						hasTable=true;
					}
					if(temp.contains("</table>")) // we hit the end of table, textTable should contain all the strings within table
					{
						hasTable=false;
						firstTable = false;

					}
					
					if(hasTable && firstTable)
					{
						if(filterPost(temp))
						{
							textTable.add(temp);
						}
					}
					
					if(temp.contains("<a href=\"https://www.mturk.com/mturk/preview") || temp.contains("<a href=\"https://www.mturk.com/mturk/accept") || temp.contains("<a href=\"https://www.mturk.com/mturk/searchbar") ) //we found a mturk link posible hit!
					{
						hit = true;	
						//gotta clean up the string
						
						String temp2 = "";
						temp2 = temp.replace("&amp;", "&");
										
						temp2 = temp2.substring(temp.indexOf("<a href=\"https:")); // lets trim the crap before <a href
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
				}  //while , end of comment post							
				
				//coming out, if my PandA is still empty, then that means there was no preview link, It's either an "accept" link or a "searchbar" link.
				// let's just assign that hitlink to PandA.
				if(PandA.equals(""))
				{
					PandA = hitLink;
				}
					
				if(hit)
				{
					cedtext = filterSmartMode(3, textTable, hitLink);
					hit = (cedtext==null)? false: true;			
					
				}
		
				// if hit = true then we have to do more stuff, if not we keep looping
				if(hit)
				{
					
					// we gotta match the link with our records to see if we've sent it before
						newLink = checkIfLinkExist(cedtext.getTextTable(), PandA, "TN",10800000, alJson,cedtext.getCED()); 
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
	
	
	/*
	 * method to pass mode value,
	 * depending on mode, we create different hit style.
	 * 
	 * The goal is to not just blatantly take from forum. 
	 *
	 */
	
	public CEDTEXT filterSmartMode(int mode, ArrayList<String> textTable, String hitLink) throws Exception
	{
		boolean hit = false; // i know I don't really need this, i'm returning ArrayList, if size = 0 then it means false
		
		//changing this to arraylist that holds object, index 1 will be text arrayList, index 2 will be CED ( I need CED now, holds value like reward, qual, etc...)

		CEDTEXT cedtext = null;
		createExportData CED = new createExportData();
		// if I have hit, lets see if I have textTable populated, if I do then I Only use textTable
		// Also need to add some logic to see if this is likely a hit, We assume if it is in <table></table> it's probably a hit.
		// but lets add additional checks so probablity is higher.
		
		//different mode. one for the above
		// the second one I want try search in mturk first, if I find I use it, if i don't find, THEN I might use textTable, depnding on random 
		//turkernation going to use mode 2
		
			if(textTable.size()>0)
			{
				if(mode ==1)
				{
					if(isAHit(textTable)) // if it's likely a hit
					{
						CED.setFoundHit(false);
						
						// if CED did not found hit, then use textTable, otherwise use create export hit
						cedtext = new CEDTEXT(createExportHit(CED, textTable),CED);

					}
					else // if we have table, but turns out it's not a hit, DO NOT continue
					{
						hit = false;
					}
				}
				// DEPRCATING MODE 2
				// Bringing Mode 2 back, Mode 2 is now basically mode 1 from before, And mode 1 now will be to just take textTable
				else if(mode==2) // even though I have textTable I'm might not use it. 
				{
										
						if(isAHit(textTable)) // if it's likely a hit
						{
							//text = textTable;
							// lets still try to create Export hit
							CED = createExportHitLinkSOUP(hitLink, textTable); 
							
							// if we don't find the hit, we will use textTable. We now need to create CED base on textTable manually (because hit is dead)
							if(!CED.isFoundHit())
							{
						//		CED = createCEDForTextTable(textTable);
							}
							
							
							// if CED did not found hit, then use textTable, otherwise use create export hit
							cedtext = new CEDTEXT(createExportHit(CED, textTable),CED);

						}
						else // if we have table, but turns out it's not a hit, DO NOT continue
						{
							hit = false;
						}
					
					
					
					/*
					CED = createExportHitLinkSOUP(hitLink, new ArrayList<String>()); 
					
					if(!CED.isFoundHit()) // if I don't find hit, either none left, or can't view because of qual 
					{
						// maybe use textTable
						Random rand = new Random();
						int  n = rand.nextInt(10) + 1;
						if(n>=7) // use textTable
						{
							if(isAHit(textTable)) // if it's likely a hit
							{
								text = textTable;							
							}
							else // if we have table, but turns out it's not a hit, DO NOT continue
							{
								hit = false;
							}
						}
						else
						{
							hit = false;
						}
					}
					//I found the hit and I can view it
					// call createExportHit to retrieve ArrayList for export style
					else
					{
						//Again textTable at second parameter doesn't matter
						text = createExportHit(CED, new ArrayList<String>());
					}
					*/
				}
				else if(mode ==3) // I definitely won't use textTable
				{
					CED = createExportHitLinkSOUP(hitLink, new ArrayList<String>()); 
					if(CED.isFoundHit()) 
					{						
						cedtext = new CEDTEXT(createExportHit(CED, new ArrayList<String>()),CED);
					}
					else
					{
						hit = false;
					}
				}
			}
			//If textTable is EMPTY, AND hit is true, this means that user posted a link without export style.
			// in this scenario I don't want to use it, because I don't want user's other comment. 
			// Instead let's call createExportHitLink
			else
			{
				//yes I'm passing textTable
				// doesn't make sense, because it takes arraylist at index 0, assuming it's title from reddit
				// but this is not reddit
				CED = createExportHitLinkSOUP(hitLink, new ArrayList<String>()); 
				
				if(!CED.isFoundHit()) // if I don't find hit, either none left, or can't view because of qual 
				{
					hit = false;
				}
				//I found the hit and I can view it
				// call createExportHit to retrieve ArrayList for export style
				else
				{
					//Again textTable at second parameter doesn't matter
					
					cedtext = new CEDTEXT(createExportHit(CED, new ArrayList<String>()),CED);
					
				}
				
			}
			
			return cedtext;
			
	}
	
	
	
	public createExportData createCEDForTextTable(ArrayList<String> a)
	{
		
		createExportData CED = new createExportData ();
		for(int i=0; i < a.size(); i++)
		{
			String temp = a.get(i).trim();
			
			
			
		}
		return CED;
	}
	
	/*
	 * take a, which is lines of comments.
	 * Lets search for text like 'title' , 'requester', 'reward', 'TO Rating'
	 * if it has most of them then high probablity it's a hit.
	 */
	public boolean isAHit(ArrayList<String> a)
	{
		int countTitle = 0;
		int countRequester = 0;
		int countReward = 0;
		int countTo = 0;
		int countDesc = 0;
		
		boolean lTitle = true;
		boolean lRequester = true;
		boolean lReward = true;
		boolean lTo = true;
		boolean lDesc = true;
		
		for(int i=0; i < a.size(); i++)
		{
			String temp = a.get(i).toLowerCase().trim();
			
			if(temp.contains("title") && lTitle)
			{
				countTitle++;
				lTitle = false;
			}
			if(temp.contains("requester") && lRequester)
			{
				countRequester++;
				lRequester = false;
			}
			if(temp.contains("reward") && lReward)
			{
				countReward++;
				lReward = false;
			}
			if(temp.contains("to rating") && lTo)
			{
				countTo++;
				lTo = false;
			}
			if(temp.contains("description") && lDesc)
			{
				countDesc++;
				lDesc = false;
			}
			
		}
		
		if(countTitle + countRequester + countReward + countTo + countDesc > 3 ) // if I have 4 out of the 5
		{
			return true;
		}
		
		return false;
		
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
		PrintWriter pw = new PrintWriter(new FileWriter("blahTN.html"));
		
		Reader r = new InputStreamReader(in);
	
		int c;
		 while((c = r.read()) != -1) 
	     {  
	           pw.print(String.valueOf((char)c)); 
	     } 
	        r.close();
			pw.close();
			
			//after writing into file we will read it.
			
			BufferedReader reader = new BufferedReader(new FileReader("blahTN.html"));	
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
			
			
			// Break out of method if, tried to find today and tomorrows link.
			if(!b && ret.equals(""))
			{
				return ret;
			}

				
			// if we don't find anything return empty which means error
			
			if(ret.equals(""))
			{
				ret = getTodayLinkTN(u, false);
			}
			
			return ret;

			
	}
	
	public String getTodayLinkTNsoup(String u, boolean b) throws Exception
	{
		
		

			Document doc = Jsoup.connect(u).userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3").get();			
			
			Elements link  = doc.getElementsByClass("threadtitle");

			
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
		    
		    
		    
			    
			 for (Element e : link) {
				 String linkText = e.text(); // "example""
			
				 
				 
				 if( linkText.toLowerCase().contains(dateFormat.format(d.getTime()))
				    		||linkText.toLowerCase().contains(dateFormat2.format(d.getTime()))
				    		||linkText.toLowerCase().contains(dateFormat3.format(d.getTime()))
				    		) //compare if it's the same
				 {
					Elements eleme =e.children();
					String s = eleme.get(0).toString();
					
					
					
			    	ret  = s.substring(s.indexOf("href="), s.indexOf("&amp")) ;
				    ret =  ret.replace("href=\"", "");
			    	break;
				 }
				 
		       }
			 
			
			 
				if(!b && ret.equals(""))
				{
					return ret;
				}

				if(ret.equals(""))
				{
					ret = getTodayLinkTNsoup(u, false);
				}
	

	
				return ret;

			
	}
	
	public void processPageMC(String u) throws Exception
	{
		String url = u;
		//String url = "http://mturkforum.com/showthread.php?6244-Can-t-Find-Good-HITs-2-10/page100";
		URL pageURL = new URL(url); 
		HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
		urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();
		
		
		InputStream in = new BufferedInputStream(urlConnection.getInputStream()); 
		PrintWriter pw = new PrintWriter(new FileWriter("blahMC2.html"));
		
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
		
			
		BufferedReader reader = new BufferedReader(new FileReader("blahMC2.html"));	
		String s;
		while((s = reader.readLine()) != null)
		{
			String temp = s.toLowerCase().trim(); //let's trim it first

			if(temp.startsWith("<blockquote class="))
			{
				boolean hit = false;
				ArrayList<String> text = new ArrayList<String>();
				CEDTEXT cedtext = null;
				String hitLink = "";
				String PandA ="";
				boolean hasTable = false;
				ArrayList<String> textTable = new ArrayList<String>();
				
				while(!temp.equals("</blockquote>")) // keep looping all the text the poster did store them
				{
					temp =reader.readLine().trim();
					
					if(temp.contains("<table class=\""))
					{						
						hasTable=true;
					}
					if(temp.contains("</table>")) // we hit the end of table, textTable should contain all the strings within table
					{
						hasTable=false;
												
						//mturk grind forum have </table> on the same line, I need to extract what's before </table>
						String t = temp.substring(0, temp.lastIndexOf("</table>")+ 8);
						if(t.contains("mturksuite.com"))
						{
							String replaceStr= t.substring(t.indexOf("<div style=\"text-align: center\">"), t.indexOf("</table>"));
							t = t.replace(replaceStr, "");
						}
						textTable.add(t);
					}
					
					if(hasTable)
					{
						if(filterPost(temp))
						{
							textTable.add(temp);
						}
					}
					
					if(temp.contains("<a href=\"https://www.mturk.com/mturk/preview") || temp.contains("<a href=\"https://www.mturk.com/mturk/accept") || temp.contains("<a href=\"https://www.mturk.com/mturk/searchbar") ) //we found a mturk link posible hit!
					{
						hit = true;	
						//gotta clean up the string
						
						String temp2 = "";
						temp2 = temp.replace("&amp;", "&");
										
						temp2 = temp2.substring(temp.indexOf("<a href=\"https:")); // lets trim the crap before <a href
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
				
				if(hit)
				{
					cedtext = filterSmartMode(1, textTable, hitLink);
					hit = (cedtext==null)? false: true;
				}
				
				// if hit = true then we have to do more stuff, if not we keep looping
				if(hit)
				{
					// we gotta match the link with our records to see if we've sent it before
						newLink = checkIfLinkExist(cedtext.getTextTable(), PandA, "MTC", 10800000, alJson,cedtext.getCED());
				}
				
				//reset hit and text
				// wait.. doesn't these 2 get reset 
			//	hit = false;
				//text.clear();
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
		File newFile = new File("blahMC2.html");
		if(newFile.exists()&& (!test))
		{
			newFile.delete();

		}

	}
	
	public void mturkCrowd() throws Exception
	{
		try{
			logger.info("<<Mturk Crowd>> Started Mturk Crowd");	
		String todayLink = getTodayLinkMC("http://www.mturkcrowd.com/forums/daily-work-threads.4/", true);
		
		if(!todayLink.equals(""))
		{
			processPageMC("http://mturkcrowd.com/"+todayLink+"/page-1000"); //1000 so its greater so it's always the last page
			
			
		}
		else
		{
			//System.out.println("error grabbing today's thread");
			window.getInstance().addText("error grabbing MC thread");
		}
		
		window.getInstance().setLblTime();
		logger.info("<<Mturk Crowd>> Finished Mturk Crowd");
		}
		catch(Exception e)
		{
			logger.info(e.getMessage());
		}
		
		
	}
	public String getTodayLinkMC(String u, boolean b) throws Exception
	{ 
		String url = u;
		//String url = "http://mturkforum.com/showthread.php?6244-Can-t-Find-Good-HITs-2-10/page100";
		URL pageURL = new URL(url); 
		HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
		urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();				
		
		
		InputStream in = new BufferedInputStream(urlConnection.getInputStream()); 
		PrintWriter pw = new PrintWriter(new FileWriter("blahMC.html"));
		
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
			
			BufferedReader reader = new BufferedReader(new FileReader("blahMC.html"));	
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
			
			File newFile = new File("blahMC.html");
			if(newFile.exists()&& (!test))
			{
				newFile.delete();			
			}
			
			// Break out of method if, tried to find today and tomorrows link.
			if(!b && ret.equals(""))
			{
				return ret;
			}
			

				
			// if we don't find anything return empty which means error
			
			if(ret.equals(""))
			{
				ret = getTodayLinkMC(u, false);
			}
			
			return ret;
		
	}
	
	public void mturkGrind() throws Exception
	{
		try{
			logger.info("<<Mturk Grind>> Started Mturk Grind");
		String todayLink = getTodayLinkMG("http://www.mturkgrind.com/forums/awesome-hits.4/", true);
		
		if(!todayLink.equals(""))
		{
			//processPageMG("http://mturkgrind.com/threads/08-11-today-is-tuesday.28431/page-52");
			processPageMG("http://mturkgrind.com/"+todayLink+"/page-1000"); //1000 so its greater so it's always the last page
			
		}
		else
		{
			//System.out.println("error grabbing today's thread");
			window.getInstance().addText("error grabbing MG thread");
		}
		
		window.getInstance().setLblTime();
		logger.info("<<Mturk Grind>> Finished Mturk Grind");
		}
		catch(Exception e)
		{
			logger.info(e.getMessage());
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
				CEDTEXT cedtext = null;
				String hitLink = "";
				String PandA ="";
				boolean hasTable = false;
				ArrayList<String> textTable = new ArrayList<String>();
				
				while(!temp.equals("</blockquote>")) // keep looping all the text the poster did store them
				{
					temp =reader.readLine().trim();
					
					if(temp.contains("<table class=\""))
					{						
						hasTable=true;
					}
					if(temp.contains("</table>")) // we hit the end of table, textTable should contain all the strings within table
					{
						hasTable=false;
												
						//mturk grind forum have </table> on the same line, I need to extract what's before </table>
						String t = temp.substring(0, temp.lastIndexOf("</table>")+ 8);
						if(t.contains("mturksuite.com"))
						{
							String replaceStr= t.substring(t.indexOf("<div style=\"text-align: center\">"), t.indexOf("</table>"));
							t = t.replace(replaceStr, "");
						}
						textTable.add(t);
					}
					
					if(hasTable)
					{
						if(filterPost(temp))
						{
							textTable.add(temp);
						}
					}
					
					if(temp.contains("<a href=\"https://www.mturk.com/mturk/preview") || temp.contains("<a href=\"https://www.mturk.com/mturk/accept") || temp.contains("<a href=\"https://www.mturk.com/mturk/searchbar") ) //we found a mturk link posible hit!
					{
						hit = true;	
						//gotta clean up the string
						
						String temp2 = "";
						temp2 = temp.replace("&amp;", "&");
										
						temp2 = temp2.substring(temp.indexOf("<a href=\"https:")); // lets trim the crap before <a href
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
				
				if(hit)
				{
					cedtext = filterSmartMode(1, textTable, hitLink);
					hit = (cedtext==null)? false: true;
				}
				
				// if hit = true then we have to do more stuff, if not we keep looping
				if(hit)
				{
					// we gotta match the link with our records to see if we've sent it before
						newLink = checkIfLinkExist(cedtext.getTextTable(), PandA, "MTG", 10800000, alJson,cedtext.getCED());
				}
				
				//reset hit and text
				// wait.. doesn't these 2 get reset 
			//	hit = false;
				//text.clear();
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
			
			// Break out of method if, tried to find today and tomorrows link.
			if(!b && ret.equals(""))
			{
				return ret;
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
		logger.info("<<REDDIT>> Started Reddit HWTF");
		timer.runHWTF = false;
		try{
		ArrayList<String> list =  new ArrayList<String>();
		String url ="http://www.reddit.com/r/HITsWorthTurkingFor/new/";
		URL pageURL = new URL(url); 
		HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
		urlConnection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
		urlConnection.addRequestProperty("User-Agent", "Mozilla");
		urlConnection.addRequestProperty("Referer", "google.com");
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();
		
		if(urlConnection.getResponseCode() == 301){ //wtf reddit redirecting me, but to the same URL
			String newUrl = urlConnection.getHeaderField("Location");
			urlConnection  = (HttpURLConnection) new URL(newUrl).openConnection();
			urlConnection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			urlConnection.addRequestProperty("User-Agent", "Mozilla");
			urlConnection.addRequestProperty("Referer", "google.com");
			urlConnection.setRequestMethod("GET");							
			urlConnection.connect();
		}
		
		
		InputStream in = new BufferedInputStream(urlConnection.getInputStream()); 
		PrintWriter pw = new PrintWriter(new FileWriter("blah3.html"));
		Reader r = new InputStreamReader(in);
		System.out.println(r.read());
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
				
				if(urlConnection.getResponseCode() == 301){ //wtf reddit redirecting me, but to the same URL
					String newUrl = urlConnection.getHeaderField("Location");
					urlConnection  = (HttpURLConnection) new URL(newUrl).openConnection();
					urlConnection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
					urlConnection.addRequestProperty("User-Agent", "Mozilla");
					urlConnection.addRequestProperty("Referer", "google.com");
					urlConnection.setRequestMethod("GET");							
					urlConnection.connect();
				}
				
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
					    		 text.add(test + "</br>");
					    		// System.out.println(new Date() + " "+ text.get(0));
					    		 
					    	 }

					    	 if(test.startsWith("a href=\"https://www.mturk.com"))
					    	 {
					    		 
					    		 String temp =  "<"+test+"</a>";
					    		 //Need to add open to new tab
					    		 temp = temp.substring(0,3) + "target=_blank " + temp.substring(3);					    		
					    		 
					    		 text.set(0, text.get(0)+ temp + "</br>"); // lets add to the post
					    		 
					    		 test = test.substring(test.indexOf("href=\"")); //trim the crap before href
					    		 test = test.substring(test.indexOf("href=\""), test.indexOf("\"", 50)); // 50 to insure we hit the " we want
					    		 test = test.replace("href=\"", "");
					    		 test = test.replace("&amp;", "&");
					    		 			
					    		 
			
								
								// we gotta match the link with our records to see if we've sent it before
								
								// don't need text anymore
								// createExportHitLink returns createExportData object, 
								// which createExportHit method takes and create an ArrayList.
					    		 
					    		 //let's take text, need title to do potential search bar hit
								createExportData CED = createExportHitLinkSOUP(test, text);
								// if I have link then I should Panda it. 
								
								if(!CED.getLink().equals(""))
								{
									
									PandA = CED.getLink().replaceAll("preview", "previewandaccept");
									CED.setPandA(PandA);
								}
								else //not able to scrape a hit link
								{
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
								}
								
								newLink = checkIfLinkExist(createExportHit(CED, text), PandA, "HWTF", 10800000, alJson, CED);												    	
					    		 
								//System.out.println(new Date() + " <<REDDIT>> "+ PandA);
								
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
			logger.info(e.getMessage());
			e.printStackTrace();
		}
	//	timer.runHWTF = true;
		logger.info("<<REDDIT>> Finished Reddit HWTF");
			
		
	}
	
	public createExportData createExportHitLinkSOUP(String u , ArrayList<String> a) throws Exception
	{			
		Document doc;
		createExportData CED = new createExportData();
		do
		{	
			doc = Jsoup.connect(u)
					   .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3").get();
			
			if(u.startsWith("https://www.mturk.com/mturk/searchbar"))
			{
				CED =  getSearchBarHitInfoSOUP(u, new createExportData(), doc);
			}		
			// have to do preview links too. If no qual I can still see the hit and grab info.
			else if(u.contains("preview"))
			{
				CED =  getPreviewHitInfoSOUP(u,a,doc);
				
			}
		}while(checkExceed(doc));
		
		
		return CED;
	}
	
	
	/*
	 * Search result may contain more then 1. There is no way of knowing which one the link is for, so Always assume the first one.
	 * 
	 * read search result, take first record and grab info like link, requester, etc...
	 */
	public createExportData getSearchBarHitInfoSOUP(String u, createExportData CEData, Document doc) throws Exception
	{
		
		String requestID = "";
		// if my URL contains requesterID I can use it to find TO rating			
		if(u.toLowerCase().contains("requesterid"))
		{
			String temp = u.substring(u.toLowerCase().indexOf("requesterid")+12);
			//Oyie.. ID could be 13 chars??
			// and apprently 15...
			
			if(temp.length()<=16) // assume requesterid is last parameter on URL , so can just grab everything
			{
				requestID  = temp;
			}
			else // requesterid is NOT the last parameter on URL... 
			{					
				requestID = temp.substring(0, temp.indexOf("&"));
			}
		
			//System.out.println("REquest ID " + requestID);
		}
		
		
		String s ="";
		Element e = doc.getElementById("alertboxHeader");
		
		Elements es = doc.getElementsByClass("error_title");  //Your search did not match any HITs.
		s = es.text();
		
		if(s.contains("Your search did not match any HITs."))
		{
			CEData.setFoundHit(false);
		}
		
		else
		{
			
			es = doc.getElementsByClass("capsulelink");
			if(es.size()>0)				
			{
				CEData.setFoundHit(true);
				s =  es.get(0).text();
				CEData.setTitle(s);												// TITLE
			//	System.out.println("Title is " + es.get(0).text());  
			}
			
			if(es.size()>0)	
			{
				s = es.get(1).childNodes().get(1).attr("href");
				if(!s.equals("")) // I may have hits that I cant' view the link to //no qual
				{
					s = "https://www.mturk.com" + s ;
					CEData.setLink(s); 											//PREVIEW LINK	
				
					//System.out.println("full link is : " + s);
				}
					
			}
			
			
			es = doc.getElementsByClass("requesterIdentity");
			if(es.size()>0)				
			{
				s =  es.get(0).text();
				CEData.setRequester(s);
				//System.out.println("Requester is : " + s);						// REQUESTER NAME
			}
			
			
			e = doc.getElementById("duration_to_complete.tooltip--0");
			if(e!=null)
			{
				e = e.parent();
				es = e.siblingElements();
				s = es.text();
				CEData.setTime(s);
				//System.out.println("time is : " + s);										// time
			}
			
			e = doc.getElementById("reward.tooltip--0");
			if(e!=null)
			{
				e = e.parent();
				es = e.siblingElements();
				s = es.text();
				CEData.setReward(s);
				//System.out.println("Reward is : " + s);										// REWARD
			}
			e = doc.getElementById("description.tooltip--0");
			if(e!=null)
			{
				e = e.parent();
				es = e.siblingElements();
				s = es.text();
				CEData.setDesc(s);
				//System.out.println("Description is : " + s);										// DESC
			}
	
			e = doc.getElementById("qualificationsRequired.tooltip--0");
			if(e!=null)
			{
				e = e.parent().parent();
				es = e.siblingElements();
				s="";
				for(Element ele : es)
				{
					s +=ele.text()+";";
				}
	
				s = s.replace("Masters", "<span style=\"color: red\"><b>Masters </b></span>");
				
				CEData.setQual(s);
			//	System.out.println("QUal is : " + s);										// QUAL
			}
			
			
			CEData.setRequesterID(requestID);
			//If I found the hit, But I'm able to grab hit links from it. let's just set link as the search page.
			if(CEData.getLink().equals(""))
			{
				CEData.setLink(u);
			}
		}
		
		
		
		return CEData;
	}
	
	public createExportData getPreviewHitInfoSOUP(String u, ArrayList<String> a, Document doc) throws Exception
	{
		createExportData CED = new createExportData();
		//link has to be regular preview link, NOT Panda
		if(u.contains("previewandaccept"))
		{
			CED.setLink(u.replaceAll("previewandaccept", "preview"));
		}
		else // just regular link
		{
			CED.setLink(u);
		}				
		String temp="";
		
		Element e = doc.getElementById("alertboxHeader");				 // qualification do not meet && There are no more available Hits

		// if there are no alertboxHeader then this probably means Exceed max allowed page. don't bother doing anything
		if(e!=null)
		{
			temp = e.text().toLowerCase();
			if(temp.contains("your qualifications do not meet") || temp.contains("there are no hits"))
			{
				CED.setFoundHit(false);			
			}
			else // we can view hit
			{			
				CED.setFoundHit(true);
				Elements es = doc.select("input[name=prevRequester]");			// requester name
				CED.setRequester(es.get(0).val());
	
				es = doc.select("input[name=requesterId]");						// requester name
				CED.setRequesterID(es.get(0).val());
				
				es = doc.select("input[name=prevReward]");						// reward
				CED.setReward(es.get(0).val().replace("USD", "$ "));
				
				es = doc.select("td.capsulelink_bold");							// title		
				CED.setTitle(es.text());
				
				es = doc.select("td.capsule_field_text");						
				CED.setTime(es.get(3).text());									// time		
				
				es.get(4).text().replace("Masters", "<span style=\"color: red\"><b>Masters </b></span>");
				CED.setQual(es.get(4).text());									// qual
				
				
				
				
			}
		}

		
		// ONLY FOR REDDIT HITS (a.size()>0)
		//Here either CED found hit or not. If it didn't then let's try and search
		// reddit hit title usually starts like US - THIS IS MY TITLE - REQUESTER - etc...
		// So I can try to take title and query search bar and see what I get.
		// better to get No hit, then wrong hit.. so adding title and requester
		
		if(!CED.isFoundHit() && a.size()>0)
		{
			String title =a.get(0);			
			 StringTokenizer st = new StringTokenizer(title, "-");
			 int count =0;
			 String title2="";
			 while(st.hasMoreTokens())
			 {
				 temp =st.nextToken();
				 if(count==1||count==2)  // 0 is ICA/US, 1 is Title, 2 is REquester, the rest I don't care
				 {
					 title2+=temp;
				 }
				 count++;
			 }
			title2= title2.replaceAll("\\s","+");
			
			String url = "https://www.mturk.com/mturk/searchbar?selectedSearchType=hitgroups&searchWords=" + title2 + "&minReward=0.00&x=0&y=0";
			
			do{
				doc = Jsoup.connect(url)
						   .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3").get();
				
				CED = getSearchBarHitInfoSOUP(url, CED, doc);
				
			}while(checkExceed(doc));

		}
		
		
		return CED;
		
	}
	
	
	/**
	 * @deprecated
	 * @param u
	 * @param a
	 * @return
	 * @throws Exception
	 */
	public createExportData createExportHitLink(String u , ArrayList<String> a) throws Exception
	{	
		
		if(u.startsWith("https://www.mturk.com/mturk/searchbar"))
		{
			return getSearchBarHitInfo(u, new createExportData());
		}		
		// have to do preview links too. If no qual I can still see the hit and grab info.
		else if(u.contains("preview"))
		{
			return getPreviewHitInfo(u,a);
			
		}
		return new createExportData();
	}
	
	/**
	 * @deprecated
	 * @param u
	 * @param a
	 * @return
	 * @throws Exception
	 */
	public createExportData getPreviewHitInfo(String u, ArrayList<String> a) throws Exception
	{
		createExportData CED = new createExportData();
		//link has to be regular preview link, NOT Panda
		if(u.contains("previewandaccept"))
		{
			CED.setLink(u.replaceAll("previewandaccept", "preview"));
		}
		else // just regular link
		{
			CED.setLink(u);
		}
		
		
		// Now have to read the preview link,
		// 3 things can happen:
		// - hit is available to be view, no qual.  I have to extract data
		// - hit has qual, can't view
		// - ran out of hits
		
		
		// HAS to be preview link, NOT PANDA
		String url = CED.getLink();
		
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
			
			s = s.trim();
			
			if(s.startsWith("Your Qualifications do not meet"))
			{
				CED.setFoundHit(false);
				break;
			}
			
			if(s.startsWith("There are no more available HITs in this group. More HITs are shown below."))
			{
				CED.setFoundHit(false);
				break;
			}
								
			if(s.startsWith("<input type=\"hidden\" name=\"prevRequester\"")) // requester name
			{
				s = s.substring(s.indexOf("value=")+6, s.lastIndexOf(">"));
				s = s.replaceAll("\"", "");
				CED.setRequester(s);
				
			//	System.out.println("Requester : " + CED.getRequester());
			}
			
			if(s.startsWith("<input type=\"hidden\" name=\"requesterId\""))  // requester ID
			{
				s = s.substring(s.indexOf("value=")+6, s.lastIndexOf(">"));
				s = s.replaceAll("\"", "");
				CED.setRequesterID(s);
				
			//	System.out.println("Requester : " + CED.getRequesterID());
			}
			
			if(s.startsWith("<input type=\"hidden\" name=\"prevReward\"")) // reward
			{
				s = s.substring(s.indexOf("value=")+6, s.lastIndexOf(">"));
				s = s.replaceAll("\"", "");
				s = s.replace("USD", "$ ");
				CED.setReward(s);
				
			//	System.out.println("Requester : " + CED.getReward());
			}
			
			
			if(s.startsWith("<div style=\"white-space:nowrap;")) //title
			{
				//next line is title
				s = reader.readLine();
				s=s.trim();
				CED.setTitle(s);
				
			//	System.out.println("Title : " + CED.getTitle());
				
			}
			
			if(s.startsWith("Duration"))
			{
				reader.readLine();
				reader.readLine();
				reader.readLine();
				s = reader.readLine();
				s = s.trim();
				
				CED.setTime(s);
				
				//System.out.println("Time : " + CED.getTime());
			}
			
			
			if(s.startsWith("Qualifications Required:"))  // QUAL
			{
				reader.readLine();
				reader.readLine();
				reader.readLine();
				reader.readLine();
				s = reader.readLine();
				
				s = s.trim();
				s = s.replaceAll(";", "</br>");
				CED.setQual(s);
				
				//System.out.println("QUAL : " + CED.getQual());
				
				break; // get out of while, currently there's no info after this we care about.
			}
			
			
		}
		
		reader.close();
		
		File f= new File(fileName);
		if(f.exists())
		{
			f.delete();
		}
		
		//Here either CED found hit or not. If it didn't then let's try and search
		// reddit hit title usually starts like US - THIS IS MY TITLE - REQUESTER - etc...
		// So I can try to take title and query search bar and see what I get.
		// better to get No hit, then wrong hit.. so adding title and requester
		
		if(!CED.isFoundHit() && a.size()>0)
		{
			String title =a.get(0);			
			 StringTokenizer st = new StringTokenizer(title, "-");
			 int count =0;
			 String title2="";
			 while(st.hasMoreTokens())
			 {
				 String temp =st.nextToken();
				 if(count==1||count==2)  // 0 is ICA/US, 1 is Title, 2 is REquester, the rest I don't care
				 {
					 title2+=temp;
				 }
				 count++;
			 }
			title2= title2.replaceAll("\\s","+");
			
			url = "https://www.mturk.com/mturk/searchbar?selectedSearchType=hitgroups&searchWords=" + title2 + "&minReward=0.00&x=0&y=0";
			
			// reset found hit 
			CED.setFoundHit(true);
			
			CED = getSearchBarHitInfo(url, CED);
		}
		
		return CED;
		
	}
	
	
	
	/*
	 * Search result may contain more then 1. There is no way of knowing which one the link is for, so Always assume the first one.
	 * 
	 * read search result, take first record and grab info like link, requester, etc...
	 */
	/**
	 * @deprecated
	 * @param u
	 * @param CEData
	 * @return
	 * @throws Exception
	 */
	public createExportData getSearchBarHitInfo(String u, createExportData CEData) throws Exception
	{
			String requestID = "";
			// if my URL contains requesterID I can use it to find TO rating			
			if(u.toLowerCase().contains("requesterid"))
			{
				String temp = u.substring(u.toLowerCase().indexOf("requesterid")+12);
				//Oyie.. ID could be 13 chars??
				// and apprently 15...
				
				if(temp.length()<=16) // assume requesterid is last parameter on URL , so can just grab everything
				{
					requestID  = temp;
				}
				else // requesterid is NOT the last parameter on URL... 
				{					
					requestID = temp.substring(0, temp.indexOf("&"));
				}
			
				//System.out.println("REquest ID " + requestID);
			}
			String url = u;
			
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
					
					s = s.trim();
					
					if(s.startsWith("Your search did not match any HITs."))
					{
						//System.out.println("No Hit Found, Hit dead");
						CEData.setFoundHit(false);
						break;
					}
					
					if(s.startsWith("<a class=\"capsulelink\"")) //next line hould be title
					{
						s = reader.readLine();
						s = s.trim();
						s = s.replaceAll("&quot;", "'");
						
						CEData.setTitle(s);
						//System.out.println("title " + CEData.getTitle());
					}
					
					if(s.contains("View a HIT in this group") && s.contains("a href")) // We may have a preview link, if qualify
					{
						s = s.substring(s.indexOf("/mturk"));
						s = "https://www.mturk.com" + s.substring(0, s.indexOf(">")-1);
						
						CEData.setLink(s);
					}
					
					if(s.startsWith("<span class=\"requesterIdentity\"")) //requester name
					{
						s = s.substring(s.indexOf(">")+1, s.lastIndexOf("<"));
						CEData.setRequester(s);
						//System.out.println("requester " + CEData.getRequester());
					}
					
					if(s.toLowerCase().startsWith("time allotted")) // time allotted
					{
						//have to read 3 lines then will have line with time.
						reader.readLine();
						reader.readLine();
						s = reader.readLine();
						
						s =s.substring(s.indexOf(">")+1);
						s = s.substring(0,s.indexOf("<"));
						
						CEData.setTime(s);
						//System.out.println("TIME " + CEData.getTime());
					}
					
					if(s.contains("<span class=\"reward\">"))  // reward amount
					{
						s = s.substring(s.indexOf("\"reward\"") + 9);
						s = s.substring(0, s.indexOf("<"));
						CEData.setReward(s);
						//System.out.println(CEData.getReward());
					}
					
					
					if(s.startsWith("Description:"))  // description
					{
						reader.readLine();
						reader.readLine();
						s = reader.readLine();
						
						s =s.substring(s.indexOf(">")+1);
						s = s.substring(0,s.indexOf("<"));
						
						CEData.setDesc(s);
						//System.out.println(CEData.getDesc());
					}
					
					if(s.startsWith("Qualifications Required:"))  // QUAL
					{
						reader.readLine();
						reader.readLine();
						reader.readLine();
						reader.readLine();
						reader.readLine();
						reader.readLine();
						s = reader.readLine();//7						
						String temp = s;
						String qual = "";
						while(!(temp.startsWith("</table>")))
						{
							temp = reader.readLine().trim();
																		
							if(temp.startsWith("<td style=\"padding-right: 2em; white-space: nowrap;\">"))
							{
								reader.readLine();								
								temp = reader.readLine();
								while(!(temp.startsWith("</td>")))
								{
									temp = reader.readLine().trim();
									if(!temp.startsWith("<"))
									{
										qual += temp.trim()+" ";
									}
								
								}
								qual = qual + "</br>";
							}
						}
						
						
						qual = qual.replaceAll("</div>", "");
						CEData.setQual(qual);
						//System.out.println(CEData.getQual());
						
						// I only assume first one, break out of while loop now,
						break;
					}
					
					
					//System.out.println(s);
					
				}
				
				//coming out of while loop, I either found a hit, with CEData populated
				// or a dead hit( a dead hit with requester ID in URL, or dead hit with absolutely nothing)
				// with empty CEData. Either case I have an object of CEData.
				// I still want to export the hit, DOA
								
				CEData.setRequesterID(requestID);
				
				//If I found the hit, But I'm able to grab hit links from it. let's just set link as the search page.
				if(CEData.getLink().equals(""))
				{
					CEData.setLink(url);
				}
				
				reader.close();
				
				
				
				File f= new File(fileName);
				if(f.exists())
				{
					f.delete();
				}
				
				return CEData;
	}
	public void TurkForum() throws Exception
	{		
		try{
			logger.info("<<Mturk Forum>> Started Mturk Forum");
		String todayLink = getTodayLink("http://mturkforum.com/forumdisplay.php?30-Great-HITS", true);
		todayLink = window.getInstance().getURL().equals("") ? todayLink : window.getInstance().getURL();
	//	todayLink =  "showthread.php?13640-Can-t-Find-FUN-HIT-s-01-30-Super-Funbowl-Friday!!";
		if(!todayLink.equals(""))
		{
			//processPage("http://mturkforum.com/showthread.php?33678-Can-t-find-Fantastic-HITs-8-11-Tantalizing-Tuesday!/page42");
			processPage("http://mturkforum.com/"+todayLink+"/page1000"); //1000 so its greater so it's always the last page
			
		}
		else
		{
			//System.out.println("error grabbing today's thread");
			window.getInstance().addText("error grabbing today's thread");
		}
		
		window.getInstance().setLblTime();
		logger.info("<<Mturk Forum>> Finished Mturk Forum");
		
		}
		catch (Exception e)
		{
			logger.info(e.getMessage());
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
				CEDTEXT cedtext=null;
				String hitLink = "";
				String PandA = "";
				
				boolean hasTable = false; 
				ArrayList<String> textTable = new ArrayList<String>();
				
				while(!temp.equals("</blockquote>")) // keep looping all the text the poster did store them
				{
					temp =reader.readLine().trim();
					
					if(temp.contains("<table class=\""))
					{						
						hasTable=true;
					}
					if(temp.contains("</table>")) // we hit the end of table, textTable should contain all the strings within table
					{
						hasTable=false;
						if(temp.contains("mturksuite.com"))
						{
							String replaceStr= temp.substring(temp.indexOf("<div style=\"text-align: center\">"), temp.indexOf("</table>"));
							temp = temp.replace(replaceStr, "");
						}
					}
					
					if(hasTable)
					{
						if(filterPost(temp))
						{
							textTable.add(temp);
						}
					}
					
					if(temp.contains("<a href=\"https://www.mturk.com/mturk/preview") || temp.contains("<a href=\"https://www.mturk.com/mturk/accept") || temp.contains("<a href=\"https://www.mturk.com/mturk/searchbar") ) //we found a mturk link posible hit!
					{
						hit = true;	
						//gotta clean up the string
						
						String temp2 = "";
						temp2 = temp.replace("&amp;", "&");
										
						temp2 = temp2.substring(temp.indexOf("<a href=\"https:")); // lets trim the crap before <a href
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
				
				if(hit)
				{
					cedtext = filterSmartMode(1, textTable, hitLink);
					hit = (cedtext==null)? false: true;
				}
				
				// if hit = true then we have to do more stuff, if not we keep looping
				if(hit)
				{
					// we gotta match the link with our records to see if we've sent it before
						newLink = checkIfLinkExist(cedtext.getTextTable(), PandA, "MTF", 10800000, alJson, cedtext.getCED());
				}
				
				//reset hit and text
				// wait.. doesn't these 2 get reset 
			//	hit = false;
				//text.clear();
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
	public boolean checkIfLinkExist(ArrayList<String> a, String l, String source, int time , ArrayList<String> jsonList, createExportData CED) throws Exception
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
		
			hitData hd = new hitData(l, new Date(), source, time, a);
			hd.mergeCED(CED);
			
			myData.getArray().add(hd);
			
			read.seralize(myData); //Write back data class
			
			//write to full data
			// pass liveData, what if in my dolivehitupdate I already deSeralize to array, its now working on the array. But I find a new hit. I just write this new hit.
			// but then in my dolivehitupdate, it will just overwrite the data. Need to use the same data object liveData
			//Don't need this anymore. NO more searlize for live HIT. It will all be in liveData in memory.
			//if program closes, we loose it, but when we restart we start fresh from myData.
			//addSeralize(readLive,hd, liveData, source);
			
			if(!liveData.contains(l))
			{
				liveData.getArray().add(hd);
			}

			
			writeToJSONPerHIT(a,l, jsonList, CED);
					
			newLink = true;
			
			
			logger.info("FOUND NEW HIT FROM " + source);
		}
		
		return newLink;

	}
	
	// searlize  hd into rd unconditionally.
	public void addSeralize(readData rd, hitData hd , data d ,String src) throws Exception
	{
		try{
			 d = rd.deSeralize();
			 if(!d.contains(hd.getLink()))
			 {
				 d.getArray().add(hd);
			 }
			rd.seralize(d);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
	}
	
	
	public void test()
		{
				
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	
		    	 while(true)
	    		 {
		    	   try{

		    			 FTP("C:\\inetpub\\wwwroot\\www3\\stats.aspx","mturkpl.us");			    		 
			    		 Thread.sleep(60000);	
		    		 }
		    	   catch(Exception e)
			    	 {
		    		   logger.info(e.getMessage());
			    		 e.printStackTrace();
			    	 }
		    		 
		    	 }
		    	 
		     }
		});  
		t1.start();
						
		}
	public void FTP(String FileName, String dir) 
	{
		File f = new File(FileName);
					
		FTPClient ftp = new FTPClient();
		FTPClientConfig conf = new FTPClientConfig(); 
		conf.setServerTimeZoneId("UTC");
		ftp.configure(conf);

		 boolean success = false;
	     int count = 0;
	     InputStream is = null;
		
	     do{
	    	 try{
				ftp.connect("doms.freewha.com");
				//ftp.login("1929831","biznfsucks11");
				//ftp.connect("mturkpl.us");
				ftp.login("www.mturkpl.us","freewebsucks11");
				//System.out.println(ftp.login("a4515727","fuckyou11"));
				ftp.setBufferSize(1024000);
				ftp.getReplyString();			
				ftp.enterLocalPassiveMode();
				ftp.changeWorkingDirectory(dir);
				ftp.setRemoteVerificationEnabled(false);
				count++;
				is = new FileInputStream(f.getPath());
															
				success = ftp.storeFile(f.getName(), is);
				
				/*
				OutputStream outputStream = ftp.storeFileStream(f.getName());
				byte[] bytesIn = new byte[4096];
				int read = 0;

				while((read = is.read(bytesIn)) != -1) {
				    outputStream.write(bytesIn, 0, read);
				}
				*/

					
				
				is.close();
				//outputStream.close();
				
				ftp.disconnect();
			
				
	    	 }
	    	 catch(Exception e)
	    	 {
	    		 try{
		    		 e.printStackTrace();
		    		 is.close();
		    		 success = false;
	    		 }
		    	catch(Exception ex)
		    	{
		    			 
		    	}
	    	 }
	     }while(!success && count < 10);

		
	}
	
	/*
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
	*/
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
	
	
	public void writeToJSON(ArrayList<String> jsonList, String file)
	{
		String start = "{\"records\":[";
		//String end = "]}";
		String end = "], \"date\": \""+ new Date() +"\"}";
		String str= "";
		
		for(int i=0; i < jsonList.size(); i++)
		{
			str += jsonList.get(i) + ",";
		}
		
		jsonList.clear(); // Let's clear it as soon as possible, don't use it after this anymore so should be safe
		
		str = str.substring(0,str.length()-1); //get rid of last character which should be ","
		
		String finalString = start+str+end;
		
		//System.out.println(finalString);
		// write to file
		try
		{
			PrintWriter pw = new PrintWriter(new FileWriter(file));
			pw.print(finalString);
			pw.close();
			
			
			if(!test)
			{
				logger.info("<<FTP>> Starting FTP...");
				FTP(file,"public_html");
				logger.info("<<FTP>> Finished FTP...");
			}
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
			logger.info("error FTP");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.info("error writing to JSON file");
		}
		
		
	}
	
	public void writeToJSONPerHIT(ArrayList<String> a, String l, ArrayList<String> jsonList , createExportData CED)
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
		str+="\",";	
		
		
		
		if(CED.getRequester().equals("")){
			str+= "\"requester\":\"" + "\" ,";
		}
		else
		{
			str+= "\"requester\":\"" +CED.getRequester().replaceAll("\"", "'").replaceAll("\\s+", " ") + "\",";	
		}
		
		if(CED.getTitle().equals("")){
			str+= "\"title\":\"" + "\" ,";
		}
		else
		{
			str+= "\"title\":\"" + CED.getTitle().replaceAll("\"", "'").replaceAll("\\s+", " ") + "\",";	
		}
		
		if(CED.getTime().equals("")){
			str+= "\"time\":\"" + "\" ,";
		}
		else
		{
			str+= "\"time\":\"" + CED.getTime().replaceAll("\"", "'").replaceAll("\\s+", " ") + "\",";	
		}
		
		if(CED.getReward().equals("")){
			str+= "\"reward\":\"" + "\" ,";
		}
		else
		{
			str+= "\"reward\":\"" + CED.getReward().replaceAll("\"", "'").replaceAll("\\s+", " ") + "\",";	
		}
		
		if(CED.getQual().equals("")){
			str+= "\"qual\":\"" + "\" ";
		}
		else
		{
			str+= "\"qual\":\"" + CED.getQual().replaceAll("\"", "'").replaceAll("\\s+", " ") + "\"";	
		}
		
				
		str+="}";
		
		jsonList.add(str);
	}
	
	
	
	/*
	 * write to array list. Each index contains 1 hit, String that has {"Post":..."link":...}
	 * 
	 */
	public void writeToJSONPerHITLive(hitData a, String l, ArrayList<String> jsonList )
	{
		//String start = "{\"records\":[";
		//String end = "]}";
		
		ArrayList<String> a1 = a.getPost();
		
		String str = "";
				
		str += "{\"Post\":\"";
	
		//each a.get(i) is one line of post
		for(int i=0; i< a1.size(); i++)
		{
			str += a1.get(i).replaceAll("\"", "'").replaceAll("\\s+", " ");
		}		
		str+="\",";		
		// add link
		str+= "\"link\":\"" + l.replaceAll("\"", "'").replaceAll("\\s+", " ");		
		str+="\",";	
		
		
		
		
		if(a.getRequester().equals("")){
			str+= "\"requester\":\"" + "\" ,";
		}
		else
		{
			str+= "\"requester\":\"" + a.getRequester().replaceAll("\"", "'").replaceAll("\\s+", " ") + "\",";	
		}
		
		if(a.getTitle().equals("")){
			str+= "\"title\":\"" + "\" ,";
		}
		else
		{
			str+= "\"title\":\"" + a.getTitle().replaceAll("\"", "'").replaceAll("\\s+", " ") + "\",";	
		}
		
		if(a.getTime().equals("")){
			str+= "\"time\":\"" + "\" ,";
		}
		else
		{
			str+= "\"time\":\"" + a.getTime().replaceAll("\"", "'").replaceAll("\\s+", " ") + "\",";	
		}
		
		if(a.getReward().equals("")){
			str+= "\"reward\":\"" + "\" ,";
		}
		else
		{
			str+= "\"reward\":\"" + a.getReward().replaceAll("\"", "'").replaceAll("\\s+", " ") + "\",";	
		}
		
		if(a.getQual().equals("")){
			str+= "\"qual\":\"" + "\" ";
		}
		else
		{
			str+= "\"qual\":\"" + a.getQual().replaceAll("\"", "'").replaceAll("\\s+", " ") + "\"";	
		}
		
		

		str+="}";
		
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

			
		// Break out of method if, tried to find today and tomorrows link.
		if(!b && ret.equals(""))
		{
			return ret;
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
	/*
	
	public void initFireBase()
	{
		try{
		FirebaseOptions options = new FirebaseOptions.Builder()
				  .setServiceAccount(new FileInputStream(System.getProperty("user.dir") + "\\4fb19db6ab0.json"))
				  .setDatabaseUrl("https://amber-fire-5449.firebaseio.com/")
				  .build();

			FirebaseApp.initializeApp(options);
		}
		catch(Exception e)
		{			
			e.printStackTrace();
		}
	}

	public void doFireBase()
	{
		try{
			
			DatabaseReference ref = FirebaseDatabase
				    .getInstance()
				    .getReference("u/admin");
			
				ref.addListenerForSingleValueEvent(new ValueEventListener() {				    
					@Override
				    public void onDataChange(DataSnapshot dataSnapshot) {
						Object post = dataSnapshot.getValue();
				        System.out.println(post);
				    }
					@Override
				    public void onCancelled(DatabaseError err) {
						String s = err.getMessage();
						System.err.println(s);

				    }
				});			
		}
		
		catch(Exception e)
		{			
			e.printStackTrace();
		}
	}
	*/
	/**
	 * Takes a mturk link.
	 * 
	 * Link can be 
	 * - preview link
	 *  .. search for id alertBox. if exist then hit is dead.
	 *  
	 * - previewandaccept
	 *  .. need to string tran out the andaccept, then check for id alertBox like above.  
	 * 
	 * - searchbar
	 *  .. check to see if there are any class = 'error_title' with text Your search did not match any HITs.
	 *  
	 *  
	 * @return true if hit is alive.
	 * 
	 * @param link - mturk link
	 * @parm time - this is used to pass to checkExceed to increment time. 
	 */
	public boolean hitAlive(String link) throws Exception
	{
		
		Document doc = null;
		boolean alive = false;
		
		if (link.contains("mturk/previewandaccept"))
		{
			link = link.replaceAll("previewandaccept", "preview");
		}		
		
		// Keep looping if exceeded max
		do
		{			
			doc = Jsoup.connect(link)
					   .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3").get();
			
			if(link.contains("/mturk/searchbar"))
			{	
				Elements e  = doc.getElementsByClass("error_title");
				
				if(e.isEmpty()) //we have a hit
				{
					alive = true;
				}										
			}
			else if(link.contains("mturk/preview"))
			{			
				Element e = doc.getElementById("alertboxHeader");
				
				//sometimes e is null?
				if(e == null || (!e.text().toLowerCase().contains("there are no hits")))
				{
					alive = true;
				}
			}
		}while(checkExceed(doc));
		
		return alive;
	}
	
	/**
	 * check to see if page hit exceed maximum allowed page.
	 * If it does then we return true, which will keep looping.
	 * 
	 * @param doc 
	 * @return true if exceed max, meaning we have to keep looping false otherwise
	 * @throws Exception
	 */
	public boolean checkExceed(Document doc) throws Exception
	{
		Elements e  = doc.getElementsByClass("error_title");
		boolean ret = false;
		if(e.text().contains("exceeded the maximum"))
		{
			logger.info("Exceeded maximum allowed page, Waiting " + (5000 + this.timeToCheckExceed) + " miliseconds");			
			Thread.sleep(5000 + this.timeToCheckExceed);
			this.timeToCheckExceed += 5000;
			
			ret = true;			
		}
		
		return ret;
	}
	
	/**
	 * runs every 5? 10? seconds?
	 * 
	 * go thru array myData  = read.deSeralize();
	 * 
	 * - Take all the ones that are not dead, and put them in another array...
	 *   ... Maybe I can skip this part and just load directly to a LIVE array everytime I add hits. 
	 *   ... readLIVE will now hold array that have these hits. Gets added in checkIfLinkExist
	 * 
	 * - remove all the dead hits, reSeralize readLive
	 * 
	 * 
	 * - import those into fire db
	 * 
	 * Needs to do this fast.
	 * 
	 */
	public void doLiveHitUpdate()
	{
		try{
			// load all not dead hits
			// only do this the first time.
			myData  = read.deSeralize();
					
			for(int i=0; i < myData.getArray().size() ; i ++)
			{
				hitData data = myData.getArray().get(i);
				boolean alive = hitAlive(data.getLink());
				
				if(alive && (!liveData.contains(data.getLink())))
				{
					liveData.getArray().add(data);
				}
			}
			this.timeToCheckExceed=0;
			
		}
	catch(Exception e)
	{
		e.printStackTrace();
		
	}
	
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	 String oldHash= "";
		    	 while(true)
	    		 {	    		 
		    		 try{		    			 
		    			 logger.info(" <<LIVE HIT>> STARTED");				    		 
		    			 //liveData = readLive.deSeralize(); don't seralize any more. liveData is now in memory don't save it (prevent overwritting)
		    				
		    				//loop backwards, as I'm removing index will change
		    				for(int j = liveData.getArray().size() - 1; j >= 0; j--){			 
		    						
		    					
		    					boolean alive = false;
		    					
		    					//if it doesn't have any text, lets create it. WHYYY
		    					if(liveData.getArray().get(j).getPost().size()==0)
		    					{
		    						ArrayList<String> text = new ArrayList<String>();
		    						CEDTEXT cedtext = null;
		    						
		    						 //The very least add the link itself as the post
		    						// For case Qualification does not match, CED.isFoundHit will be false, in this secnario i still dont set POST
		    						// but it's still consider a live hit
		    						
		    						
		    						text.add(("<a href =' " + liveData.getArray().get(j).getLink() + " ' target='_blank'>" + liveData.getArray().get(j).getLink() + "</a>").replaceAll("previewandaccept", "preview"));
		    					
		    						liveData.getArray().get(j).setPost(text);
		    						
		    	
		    						
		    						cedtext = filterSmartMode(3, new ArrayList<String>(), liveData.getArray().get(j).getLink());

		    						if(cedtext!=null) 
		    						{
		    							alive = true;
		    							
		    							liveData.getArray().get(j).setPost(text);
		    						}
		    					}
		    					
		    				
		    					 alive = alive || hitAlive(liveData.getArray().get(j).getLink());		    					 
		    					 
		    					if(!alive) //if not alive then we remove it.
		    					{		    		
		    						
		    						logger.info("<<LIVE HIT>> Removing hit " + liveData.getArray().get(j).getLink());
		    						liveData.getArray().remove(j);
		    					}
		    				}
		    				
		    				timeToCheckExceed = 0;
		    						    				
		    				//At this point I've removed all dead hits, seralize back to file
		    				//readLive.seralize(liveData);	
		    				logger.info("<<LIVE HIT>> FINISHED");	 
			    
			    		 //check if they are different, if they are different then that means there is a difference so we pass to firebase db
			    		 String newHash = liveData.getHashString();
			    		 if(!newHash.equals(oldHash))
			    		 {
			    			 logger.info("Is different need to upload");
			    			 ArrayList<String> jsonArrayLive = new ArrayList<String>();
			    			 for(int i = 0; i < liveData.getArray().size(); i ++)
			    			 {			    							    		
			    				 writeToJSONPerHITLive(liveData.getArray().get(i), liveData.getArray().get(i).getLink(), jsonArrayLive);
			    			 }
			    			 			    			 
			    			 writeToJSON(jsonArrayLive,jsonFileLive);
			    			 logger.info("<<LIVE HIT>> number of live hits : " + liveData.getArray().size());
			    		 }
			    		 oldHash = newHash;
			    		 
			    		 Thread.sleep(8000);
			    		 		    					    		
		    		 }
		    		 catch(Exception e)
			    	 {
		    			 logger.info("error " + e.getMessage());
			    		 e.printStackTrace();
			    	 }		    		 		    		
		    		 
		    	 }
		    	
		     }
		});  
		t1.start();
		
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
	
		
		public class CEDTEXT{
		  public final ArrayList<String> a;
		  public final createExportData CED;

		  public CEDTEXT(ArrayList<String> a, createExportData CED) {
		    this.a = a;
		    this.CED = CED;
		  }
		  
		  public ArrayList<String> getTextTable()
		  {
			  return this.a;
		  }
		  public createExportData getCED()
		  {
			  return this.CED;
		  }
		}
	
}

class GMailAuthenticator extends Authenticator {
     String user;
     String pw;
     public GMailAuthenticator (String username, String password)
     {
        super();
        this.user = username;
        this.pw = password;
     }
    public PasswordAuthentication getPasswordAuthentication()
    {
       return new PasswordAuthentication(user, pw);
    }
}

