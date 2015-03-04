package part1;

public class Process {
	private String IP;
	private int port;
	private String ID;
	public Process( String IP, int port, String ID) {
		this.IP = IP;
		this.port=port; 
		this.ID = ID;
	}
	public String getIP(){
		return IP ;
	}
	public int getPort(){
		return port ;
	}
	public String getID() {
		return ID;
	}
	public void send(Message m){
		//TODO send message
	}
}
