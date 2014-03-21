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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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
		 
		 
		 //set north menu:
		
		 JPanel pnlNorth = new JPanel();
		 
		 // north east
		 JPanel pnlNorthEast = new JPanel();			// holdds time label, and clear button
		 pnlNorthEast.setLayout(new BorderLayout());
		 pnlNorthEast.add(BorderLayout.NORTH, lblTime);
		 pnlNorthEast.add(BorderLayout.SOUTH, btnClear);
		 
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
		 
		  p.setPreferredSize(new Dimension(500,600));
		  //panel.setPreferredSize(new Dimension(500,600));
		  scroller.setPreferredSize(new Dimension(500,600));
		  
		 
		  setupActionListener();
		   p.setEditable(false);

		   
		   //This is to delete data.ser file after application closes.
		    WindowAdapter listener = new WindowAdapter() {		      
		        @Override
		        public void windowClosing(WindowEvent e) {
		            readData read = new readData();
		            read.DeleteDataFile();
		        }		      
		    }; 
			 
		    
		  //set up frame 
		  frame = new JFrame("");
		  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE ); 
		  //frame.getContentPane().add(panel, BorderLayout.CENTER);
		  frame.getContentPane().add(scroller, BorderLayout.CENTER);
		  
		  frame.getContentPane().add(pnlNorth, BorderLayout.NORTH);
		   frame.addWindowListener(listener);		  
		  
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
	public void setLblTime(){
	    DateFormat dateFormat = new SimpleDateFormat("h:mm:ss");
		 Date d = new Date();
		lblTime.setText(dateFormat.format(d));
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
	
	/*
	 * Inner timer class, its to update the time 
	 */
	
	public class timeThread extends TimerTask {
		
		 public void run() {
			 window.getInstance().setLblTime();
		 }
		 
	
	}
	
}