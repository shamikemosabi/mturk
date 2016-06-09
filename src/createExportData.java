public class createExportData
{
	String title="";
	String link = "";
	String requester = "";
	String requesterURL = "";
	String requesterID = "";
	String PandA  = "";
	String reward = "";
	String desc  = "";
	String time = "";
	String hitsAva =""; // only ava when log in... damn it
	String qual = "";
	
	boolean foundHit;
	
	
	public createExportData(String t, String l, String req, String reqURL, String reqID, String p)
	{
		title=t;
		link = l;
		requester = req;
		requesterURL = reqURL;
		requesterID = reqID;
		PandA  = p;	
	}
	
	
	public createExportData()
	{
		 title="";
		 link = "";
		 requester = "";
		 requesterURL = "";
		 requesterID = "";
		 PandA  = "";
		 foundHit = false;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getLink() {
		return link;
	}


	public void setLink(String link) {
		this.link = link;
	}


	public String getRequester() {
		return requester;
	}


	public void setRequester(String requester) {
		this.requester = requester;
	}


	public String getRequesterURL() {
		return requesterURL;
	}


	public void setRequesterURL(String requesterURL) {
		this.requesterURL = requesterURL;
	}


	public String getRequesterID() {
		return requesterID;
	}


	public void setRequesterID(String requesterID) {
		this.requesterID = requesterID;
	}


	public String getPandA() {
		return PandA;
	}


	public void setPandA(String pandA) {
		PandA = pandA;
	}


	public String getReward() {
		return reward;
	}


	public void setReward(String reward) {
		this.reward = reward;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public String getHitsAva() {
		return hitsAva;
	}


	public void setHitsAva(String hitsAva) {
		this.hitsAva = hitsAva;
	}


	public String getQual() {
		return qual;
	}


	public void setQual(String qual) {
		this.qual = qual;
	}


	public boolean isFoundHit() {
		return foundHit;
	}


	public void setFoundHit(boolean foundHit) {
		this.foundHit = foundHit;
	}
	
	
}
