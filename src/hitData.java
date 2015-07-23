import java.io.Serializable;
import java.util.Date;
	/*
	 * data type, holds link and date of hit
	 */
	public class hitData implements Serializable
	{
		String link;
		Date d ;
		public hitData()
		{
			link="";
			d = new Date();
		}
		public hitData(String s, Date dtm)
		{
			link=s;
			d = dtm;
		}
		
		public void setLink(String s)
		{
			link = s;
		}
		public void setDate(Date dtm)
		{
			d = dtm;
		}
		
		public String getLink()
		{
			return link;
		}
		public Date getDate()
		{
			return d;
		}
	}