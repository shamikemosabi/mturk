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
		
		String title;
		String requester;
		String time;
		String reward;
		String qual;
		
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
		
		/**
		 * ]
		 * @param CED - There rewards, requester, title etc... data that I need
		 */
		public void mergeCED(createExportData CED)
		{
			this.title = CED.getTitle();
			this.requester = CED.getRequester();
			this.time = CED.getTime();
			this.reward = CED.getReward();
			this.qual = CED.getQual();			
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
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getRequester() {
			return requester;
		}
		public void setRequester(String requester) {
			this.requester = requester;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getReward() {
			return reward;
		}
		public void setReward(String reward) {
			this.reward = reward;
		}
		public String getQual() {
			return qual;
		}
		public void setQual(String qual) {
			this.qual = qual;
		}
	}