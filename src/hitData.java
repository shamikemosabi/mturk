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
		int timeExpire; //milliseconds when expired
		public hitData()
		{
			link="";
			d = new Date();
			src ="";
			timeExpire=0;
		}
		public hitData(String s, Date dtm, String sr, int t)
		{
			link=s;
			d = dtm;
			src = sr;
			timeExpire = t;
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
		public void setTimeExpire(int i)
		{
			timeExpire= i;
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
		public int getTimeExpire()
		{
			return timeExpire;
		}
	}