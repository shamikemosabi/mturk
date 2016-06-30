import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.google.firebase.*;
import com.google.firebase.database.*;





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
			
			
			//test2();	
			
			main task = new main();
					
			//task.initFireBase();
			//task.doFireBase();
			task.doRedditHWTF();
			//task.doTurkerNation();
			task.doForum();
			task.doMturkList();
			
			task.doLiveHitUpdate();
			task.doWriteFTP(); 
				
			//task.test();
		}
		catch(Exception e)
		 {
		 	System.out.println(e.getMessage());
		 	e.printStackTrace();
		 }
	 
		
	}	
	
	
	public static void test2() 
	{
		try{
			
			ArrayList<String> a = new ArrayList<String> ();
					
			a.add("https://www.mturk.com/mturk/searchbar?selectedSearchType=hitgroups&searchWords=Text+and+Image+Classification&minReward=0.00&requiresMasterQual=on&x=0&y=0");
			a.add("https://www.mturk.com/mturk/searchbar?selectedSearchType=hitgroups&searchWords=David+Akers&minReward=0.00&x=0&y=0");
			a.add("https://www.mturk.com/mturk/searchbar?selectedSearchType=hitgroups&requesterId=A2F2QDJJQD3MSM");
			
			
			
			for(int i=0; i<a.size() ;i++)
			{
				Document doc = Jsoup.connect(a.get(i))
						.userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3").get();			
				/*
			     
				Element e = doc.getElementById("alertboxHeader");				 // qualification do not meet && There are no more available Hits
				String temp = e.text().toLowerCase();
				createExportData CED = new createExportData();
				
				if(temp.contains("your qualifications do not meet") || temp.contains("there are no hits"))
				{
					CED.setFoundHit(false);			
				}
				else // we can view hit
				{
					Elements es = doc.select("input[name=prevRequester]");			// requester name
					System.out.println();
					CED.setRequester(es.get(0).val());

					es = doc.select("input[name=requesterId]");						// requester name
					System.out.println();
					CED.setRequesterID(es.get(0).val());
					
					es = doc.select("input[name=prevReward]");						// reward
					System.out.println(es.get(0).val().replace("USD", "$ "));	
					CED.setReward(es.get(0).val().replace("USD", "$ "));
					
					es = doc.select("td.capsulelink_bold");							// title
					System.out.println();			
					CED.setTitle(es.text());
					
					es = doc.select("td.capsule_field_text");						
					System.out.println();							// time
					CED.setTime(es.get(3).text());
					System.out.println(es.get(4).text());							//qual			
					CED.setQual(es.get(4).text());
					
				}
				  */
				
				createExportData CEData = new createExportData();
				
				
				String s ="";
				Element e = doc.getElementById("alertboxHeader");
				
				Elements es = doc.getElementsByClass("error_title");  //Your search did not match any HITs.
				System.out.println(es.text());
				
				
				es = doc.getElementsByClass("capsulelink");
				if(es.size()>0)				
				{
					s =  es.get(0).text();
					CEData.setTitle(s);												// TITLE
					System.out.println("Title is " + es.get(0).text());  
				}
				
				if(es.size()>0)	
				{
					s = es.get(1).childNodes().get(1).attr("href");
					if(!s.equals("")) // I may have hits that I cant' view the link to //no qual
					{
						s = "https://www.mturk.com" + s ;
						CEData.setLink(s); 											//PREVIEW LINK	
					
						System.out.println("full link is : " + s);
					}
						
				}
				
				
				es = doc.getElementsByClass("requesterIdentity");
				if(es.size()>0)				
				{
					s =  es.get(0).text();
					CEData.setRequester(s);
					System.out.println("Requester is : " + s);						// REQUESTER NAME
				}
				
				
				e = doc.getElementById("duration_to_complete.tooltip--0");
				if(e!=null)
				{
					e = e.parent();
					es = e.siblingElements();
					s = es.text();
					CEData.setTime(s);
					System.out.println("time is : " + s);										// time
				}
				
				e = doc.getElementById("reward.tooltip--0");
				if(e!=null)
				{
					e = e.parent();
					es = e.siblingElements();
					s = es.text();
					CEData.setReward(s);
					System.out.println("Reward is : " + s);										// REWARD
				}
				e = doc.getElementById("description.tooltip--0");
				if(e!=null)
				{
					e = e.parent();
					es = e.siblingElements();
					s = es.text();
					CEData.setDesc(s);
					System.out.println("Description is : " + s);										// DESC
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
					System.out.println("QUal is : " + s);										// QUAL
				}
				
						
			
			}
			
		

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	public static void test() 
	{				
		try{
			FirebaseOptions options = new FirebaseOptions.Builder()
					  .setServiceAccount(new FileInputStream(System.getProperty("user.dir") + "\\4fb19db6ab0.json"))
					  .setDatabaseUrl("https://amber-fire-5449.firebaseio.com/")
					  .build();

			FirebaseApp.initializeApp(options);
			

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
				
				
			//	FirebaseDatabase database =	FirebaseDatabase.getInstance();

				
				
		   /* 
		    dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
		         @Override
		         public void onDataChange(DataSnapshot snapshot) {
		             System.out.println("data");
		         }

		         @Override
		         public void onCancelled() {
		             System.err.println("Listener was cancelled");
		         }
		    });
		    */
		    System.out.println("hi");
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
			else if(isTimeBetweenTwoTime("07:00:00","24:00:00")) //7 am - 5 pm
			{
				System.out.println(new Date() +" Between 7 - 12");
				mod1 = 1;
				mod2 = rand.nextInt(30)+1;
				
				System.out.println(mod1 + " " + mod2);
				
				min =  (min * mod1) + (mod2*1000);
																
				
			}	
			else if(isTimeBetweenTwoTime("24:00:00","03:00:00")) //12 am - 3 am
			{
				System.out.println(new Date() +" Between 12 - 3");
				mod1 = rand.nextInt(3)+1;
				mod2 = rand.nextInt(30)+1;
				
				System.out.println(mod1 + " " + mod2);
				
				min =  (min * mod1) + (mod2*1000);
					
			}
			/*
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
			*/
	
			
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println((float)min/60000);

		return min;
		
	}
	
	
	/*
	 * Turker nation actively block IP address 
	 */
	public static int timeIntervalTN()
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
				min = timeInterval(5,20);
				
				
			}
			else if(isTimeBetweenTwoTime("04:00:00","05:30:00")) //4 AM - 5:30 AM
			{
			
				min = timeInterval(5,20);
				
				
			}
			else if(isTimeBetweenTwoTime("05:30:00","07:00:00")) 
			{
			
				min = timeInterval(5,15);
				
				
			}
			else if(isTimeBetweenTwoTime("07:00:00","09:00:00")) //7 am - 9 am
			{
				
				mod2 = rand.nextInt(30)+1;
				
				mod1 = timeInterval(3,10);
				min =  mod1 + (mod2*1000);
																
				
			}	
			else if(isTimeBetweenTwoTime("09:00:00","18:00:00")) //9 am - 6 pm
			{
				
				mod2 = rand.nextInt(30)+1;
				
				mod1 = timeInterval(2,4);
				min =  mod1 + (mod2*1000);
																
				
			}	
			else if(isTimeBetweenTwoTime("18:00:00","24:00:00")) //6 am - midnight
			{
				min = timeInterval(5,20);
					
			}
			else if(isTimeBetweenTwoTime("24:00:00","03:00:00")) //midnight - 3 am
			{
				min = timeInterval(5,20);
					
			}


		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		

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
	
	public static class Post {
		public String author;
	    public String title;
	    public Post(String author, String title) {
	    	this.author = author;
	    	this.title = title;
	    }
	}
	
	
}