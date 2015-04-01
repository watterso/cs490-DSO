package part1;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

public class Process implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5625734327622456589L;
	private String mIp;
	private int mPort;
	private String mId;
	public int currMesg;
	
	public Process(String ip, int port, String id) {
		this.mIp = ip;
		this.mPort = port; 
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
		//TODO send message via sockets
		//TODO fail silently when message doesn't send correctly
		try {
			Socket connection = new Socket(mIp, mPort);
			ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
			output.flush(); //always flush after creating the stream
			output.writeInt(1); //the int is the message type
			output.flush();
			output.reset();
			output.writeObject(m);
			output.flush();
			output.reset(); //needed if not serializable
			output.close(); //one time connection
			connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
