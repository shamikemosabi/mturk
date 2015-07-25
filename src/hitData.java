import java.io.Serializable;
import java.util.Date;
	/*
	 * data type, holds link and date of hit
	 */
	public class hitData implements Serializable
	{
		String link;
		Date d ;
		String src;
		public hitData()
		{
			link="";
			d = new Date();
			src ="";
		}
		public hitData(String s, Date dtm, String sr)
		{
			link=s;
			d = dtm;
			src = sr;
		}
		
		public void setLink(String s)
		{
			link = s;
		}
		public void setDate(Date dtm)
		{
			d = dtm;
		}
		public void setSrc(String sr)
		{
			src = sr;
		}
		
		public String getLink()
		{
			return link;
		}
		public Date getDate()
		{
			return d;
		}
		public String getSrc()
		{
			return src;
		}
	}