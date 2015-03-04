package part1;

public class Process {
	
	private String mIp;
	private int mPort;
	private String mId;
	
	public Process(String ip, int port, String id) {
		this.mIp = ip;
		this.mPort=port; 
		this.mId = id;
	}
	public String getIP(){
		return mIp ;
	}
	public int getPort(){
		return mPort ;
	}
	public String getID() {
		return mId;
	}
	public void send(Message m){
		//TODO send message sockets etc
	}
}
