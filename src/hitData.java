import java.io.Serializable;
import java.util.ArrayList;
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
		ArrayList<String>  post;
		
		public hitData()
		{
			link="";
			d = new Date();
			src ="";
			timeExpire=0;
			post = new ArrayList<String>();
		}
		public hitData(String s, Date dtm, String sr, int t, ArrayList<String>  p)
		{
			link=s;
			d = dtm;
			src = sr;
			timeExpire = t;
			post= p;
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
		public void setPost(ArrayList<String> p)
		{
			this.post = p;
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
		public ArrayList<String> getPost()
		{
			return this.post;
		}
	}