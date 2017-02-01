import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLDocument;



public class window
{

	private static window instance = null;
	
	private JFrame frame;
	
	private JEditorPane p;
	//private StyledDocument doc;
	private HTMLEditorKit kit;
	private HTMLDocument doc;
	
	private JScrollPane scroller;
	private 	JPanel panel;
	
	private JTextField txtURL;
	
	private JLabel lblTime;
	private JButton btnClear;
	
	private JCheckBox chkSound;
	private boolean playSound = false;
	
	private JCheckBox chkPause ;
	
	private JLabel lblTH;
	private JLabel lblMTC;
	private JLabel lblMTF;
	private JLabel lblHWTF;
	private JLabel lblFTP;
	
	private Date dtmTH;
	private Date dtmMTC;
	private Date dtmMTF;
	private Date dtmHWTF;
	private Date dtmFTP;
	
	boolean VPN = false;
	
	public window()
	{
		p = new JEditorPane();
		txtURL = new JTextField(40);
		lblTime = new JLabel();
		btnClear = new JButton("Clear");
		 panel =  new JPanel();
		 kit  = new HTMLEditorKit();
		 doc    = new HTMLDocument();
		 p.setEditorKit(kit);		 		
		 p.setDocument(doc);
		 
		 
		 
		 scroller = new JScrollPane(p);
		 scroller.add(panel);
		 //panel.add(scroller);
		 
		 //label for when it was last ran.
		 lblTH = new JLabel();
		 lblMTC= new JLabel();
		 lblMTF= new JLabel();
		 lblHWTF= new JLabel();
		 lblFTP= new JLabel();
		 
		 //set north menu:
		
		 JPanel pnlNorth = new JPanel();
		 
		 // north east
		 JPanel pnlNorthEast = new JPanel();			// holdds time label, and clear button
		 
		 BoxLayout boxLayout = new BoxLayout(pnlNorthEast, BoxLayout.Y_AXIS); // top to bottom
		 
		 pnlNorthEast.setLayout(boxLayout);
		 
		 pnlNorthEast.add(lblTH);
		 pnlNorthEast.add(lblMTC);
		 pnlNorthEast.add(lblMTF);		 
		 pnlNorthEast.add(lblFTP);
		 pnlNorthEast.add(lblHWTF);		 
		 pnlNorthEast.add(lblTime);		 
		 pnlNorthEast.add(btnClear);
		 
		 //northwest:
		 JPanel pnlNorthWest = new JPanel();					// holds URL textbox and checkbox panel
		 pnlNorthWest.setLayout(new BorderLayout());
		 pnlNorthWest.add(BorderLayout.NORTH,txtURL);
		 JPanel pnlCheckBox = new JPanel();
		
		 chkSound = new JCheckBox("Sound");
		 chkPause = new JCheckBox("Pause");
		 
		 pnlCheckBox.add(chkSound);
		 pnlCheckBox.add(chkPause);
		
		 pnlNorthWest.add(BorderLayout.SOUTH, pnlCheckBox);
		 pnlNorthWest.add(BorderLayout.SOUTH, pnlCheckBox);
		 
		 chkPause.isSelected();
		 
		 pnlNorth.setLayout(new BorderLayout());
		 pnlNorth.add(BorderLayout.WEST, pnlNorthWest);
		 pnlNorth.add(BorderLayout.EAST, pnlNorthEast);
		 //set north menu:
		 
		  p.setPreferredSize(new Dimension(530,400));
		  //panel.setPreferredSize(new Dimension(500,600));
		  scroller.setPreferredSize(new Dimension(530,400));
		  
		 
		  setupActionListener();
		   p.setEditable(false);

		   
		   //This is to delete data.ser file after application closes.
		   // Don't want to DELTE this anymore!, main.java has cleanHit() that will clean up hits
		   /*
		    WindowAdapter listener = new WindowAdapter() {		      
		        @Override
		        public void windowClosing(WindowEvent e) {
		            readData read = new readData();
		            read.DeleteDataFile();
		        }		      
		    }; 
			 */
		    
		  //set up frame 
		  frame = new JFrame("");
		  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE ); 
		  //frame.getContentPane().add(panel, BorderLayout.CENTER);
		  frame.getContentPane().add(scroller, BorderLayout.CENTER);
		  
		  frame.getContentPane().add(pnlNorth, BorderLayout.NORTH);
	//	   frame.addWindowListener(listener);		  
		  
		  frame.setLocationRelativeTo(null); 
		  frame.pack();
		  frame.setVisible(true);

		  //set up frame 
		  
		  
		  // start up time thread
		
		  
		  TimerTask task = new timeThread();			
		  Timer timer = new Timer();			
		  timer.scheduleAtFixedRate(task,new Date(), 1000); 		  

		  
	}
	
	public void setupActionListener()
	{
		
		// Adds ability for user to click hyperlinks For p (JEditorPane)
		p.addHyperlinkListener(new HyperlinkListener() {
		    public void hyperlinkUpdate(HyperlinkEvent e) {
		        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		        	if(Desktop.isDesktopSupported()) {
		        		  try {
		        			  Desktop.getDesktop().browse(e.getURL().toURI());
		        		  }
		        		  catch(Exception rer)
		        		  {
		        			  
		        		  }
		        	}
		        }
		    }
		});
		
		//Actionlistner for clear button
		btnClear.addActionListener(new ActionListener() {			
           public void actionPerformed(ActionEvent e)
           {
        	   
        	   p.setText("");        	   
              
           }
        });     
		
		//Listener for sound checkbox
		chkSound.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getSource()==chkSound)
				{
					playSound = !playSound;
				}
			}
		});
		
		
	}
	
	public void checkStuckThreads()
	{
		String[] array = {"FTP", "MTF", "TH", "HWTF", "MTC"};
		
		for(int i = 0; i < array.length ; i++)
		{
			if(isThreadStuck(array[i]) && (!VPN) )
			{
				sendText("6462841208@tmomail.net",array[i]+" Stuck","Thread stuck");
				VPN = true; // used so we only alert once
			}
		}
	}

	public boolean isThreadStuck(String src)
	{
		boolean ret = false;
		try{				
			long MILLIS_LIMIT = 3600000; // 1 hr default			
			Date d = new Date();
		 
			if(src.equals("FTP") && !lblFTP.getText().equals(""))
			{
			  MILLIS_LIMIT = 1800000; //half an hr
			  ret =  Math.abs(d.getTime() - dtmFTP.getTime()) > MILLIS_LIMIT;				
			}
			else if (src.equals("MTF") && !lblMTF.getText().equals(""))
			{
				  MILLIS_LIMIT = 3600000;  
				  ret =  Math.abs(d.getTime() - dtmMTF.getTime()) > MILLIS_LIMIT;
			}
			else if (src.equals("TH") && !lblTH.getText().equals(""))
			{
				  MILLIS_LIMIT = 3600000;
				  ret =  Math.abs(d.getTime() - dtmTH.getTime()) > MILLIS_LIMIT;
				
			}
			else if (src.equals("HWTF") && !lblHWTF.getText().equals(""))
			{
				 MILLIS_LIMIT = 1800000; //half an hr			  			  
				  System.out.println(Math.abs(d.getTime() - dtmHWTF.getTime()));
				  
				  ret =  Math.abs(d.getTime() - dtmHWTF.getTime()) > MILLIS_LIMIT;
			}
			else if (src.equals("MTC") && !lblMTC.getText().equals(""))
			{
				  MILLIS_LIMIT = 3600000; 		  
				  ret =  Math.abs(d.getTime() - dtmMTC.getTime()) > MILLIS_LIMIT;				
			}
		}
		catch(Exception e)
		{
		   e.printStackTrace();			 
		}
		 
		
		return ret;
	}
	
	public void setLblTime(String src){
	    DateFormat dateFormat = new SimpleDateFormat("h:mm:ss");
		Date d = new Date();		
				
		switch (src) {
		 case "MTF": 
			 lblMTF.setText(src +" "+ dateFormat.format(d));
			 dtmMTF = d;
         	 break;
		 case "TH": 
			 lblTH.setText(src +" "+ dateFormat.format(d));
			 dtmTH = d;
         	 break;	 
		 case "MTC": 
			 lblMTC.setText(src +" "+ dateFormat.format(d));
			 dtmMTC = d;
         	 break;
		 case "FTP": 
			 lblFTP.setText(src +" "+ dateFormat.format(d));
			 dtmFTP = d;
         	 break;
		 case "HWTF": 
			 lblHWTF.setText(src +" "+ dateFormat.format(d));
			 dtmHWTF = d;
         	 break;         	 
		 default:
			 lblTime.setText(dateFormat.format(d));
			 checkExceedVPN();
			 checkStuckThreads();
		 	break;		
		}
		
		
		/* DOn't need this anymore
		// lets delete data.ser file name every hour.
		// doing it here because I already have a timer created for lblTime
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		int minutes = calendar.get(Calendar.MINUTE);
		
		//delete once every hour
		if(minutes==30)
		{
			File f = new File(readData.fileName);
			f.delete();			
		}
		*/	


	}
	public String getURL()
	{
		return txtURL.getText();
		
	}
	public static window getInstance()
	{
		if(instance ==null)
		{
			instance = new window();				
			
		}
		return instance;
	}
	
	public boolean PlaySound()
	{
		return playSound;
	}
	public boolean Pause()
	{
		return chkPause.isSelected();
	}

	
	public void addLinkToPanel(ArrayList<String> a, String l) throws Exception
	{		
		
		for( int i = 0; i< a.size(); i++)
		{			  
			 kit.insertHTML(doc, doc.getLength(), a.get(i), 0, 0, null);				 

		}
		
		String hitLink=   " <a href=\""+l+"\" target=\"_blank\"><font color=\"red\"> CLICK HERE!!! </font></a>";
		// add hit link
		kit.insertHTML(doc, doc.getLength(), hitLink, 0, 0, null);
		kit.insertHTML(doc, doc.getLength(), "-------------------------------------------------------------------------------------------------------------------", 0, 0, null);
		//kit.insertHTML(doc, doc.getLength(), "<br><br>", 0, 0, null);
		 p.setCaretPosition(p.getDocument().getLength()); // scroll all the way to the bottom.
	}
	
	public void addText(String s ) 
	{
		try{
			kit.insertHTML(doc, doc.getLength(), s, 0, 0, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}


	public boolean checkExceedVPN()
	 {		
		String temp = "";
		try{
			URL url = new URL("http://checkip.amazonaws.com/");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			temp  = br.readLine();
			
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
								
		if(!temp.contains("DOC") || VPN)
		{
			return true;

		}
		
		VPN = true;
		
		sendText("6462841208@tmomail.net","VPN exceeded","VPN exceeded");
		
		return true;	     
	 }
	 
	 public void sendText(String textTo, String subject, String body)
	 {
		 try{
		 
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
		    String[] to = {textTo}; // added this line
		    
		   
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
			    
		    message.setSubject(subject);
		   //message.setContent(body, "text/html");
		    message.setContent(body, "text/plain");
		    Transport transport = session.getTransport("smtp");
		    transport.connect(host, from, pass);
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }

	 }
	
	
	/*
	 * Inner timer class, its to update the time 
	 */
	
	public class timeThread extends TimerTask {
		
		 public void run() {
			 window.getInstance().setLblTime("");
		 }
		 
	
	}
	
}