import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class SendEmail
{
	
	public SendEmail(ArrayList<String> a, String s) throws Exception
	{
		 	String host = "smtp.gmail.com";
		    String from = "mturkbot@gmail.com";
		    String pass = "mturkbotpassword";
		    Properties props = System.getProperties();
		    
		    
		    props.put("mail.smtp.starttls.enable", "true"); // added this line
		    props.put("mail.smtp.host", host);
		    props.put("mail.smtp.user", from);
		    props.put("mail.smtp.password", pass);
		    props.put("mail.smtp.port", "587");
		    props.put("mail.smtp.auth", "true");

		    String[] to = {"shamikemosabi@gmail.com"}; // added this line

		    Session session = Session.getDefaultInstance(props, null);
		    MimeMessage message = new MimeMessage(session);
		    message.setFrom(new InternetAddress(from));

		    InternetAddress[] toAddress = new InternetAddress[to.length];

		    // To get the array of addresses
		    for( int i=0; i < to.length; i++ ) { // changed from a while loop
		        toAddress[i] = new InternetAddress(to[i]);
		    }

		    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
		        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
		    }
		    message.setSubject(s);
		    String body="";
		    for( int i = 0; i< a.size(); i++)
		    {
		    	body = body+a.get(i) + System.getProperty("line.separator");
		    }
		    message.setContent(body, "text/html");
		    Transport transport = session.getTransport("smtp");
		    transport.connect(host, from, pass);
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
		    
		
	}
}