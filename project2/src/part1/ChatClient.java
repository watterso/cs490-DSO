package part1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import part2.FrbBroadcast;

public class ChatClient extends Thread implements BroadcastReceiver {
	
	public enum BROADCAST_TYPE{
		RELIABLE,
		FIFO
	};
	
	private Process mProcess;
	private Broadcast mBroadcast;
	
	ServerSocket mSocket;
	
	public ChatClient(String serverIp, int serverPort, String userId, int listenPort) throws IOException, ClassNotFoundException{
		String myIp = InetAddress.getLocalHost().getHostAddress();
		mProcess = new Process(myIp, listenPort, userId);
		mSocket = new ServerSocket(listenPort);
		mBroadcast = new RbBroadcast();
		mBroadcast.init(mProcess, this);
		setupServer(serverIp, serverPort);
		
		this.run();
	}
	
	public void run(){
		try {
			while(true){
				Socket sock = mSocket.accept();
				ObjectInputStream input = new ObjectInputStream(sock.getInputStream());
				int mtype = input.readInt();
				if(mtype == 1){ //next object is a message
					Message m = (Message)input.readObject();
					this.receive(m);
				}else if(mtype == 0){ //next object is a process to be added to the broadcast list
					Process p = (Process)input.readObject();
					mBroadcast.addMember(p);
				}
				input.close();
				sock.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ChatClient(String serverIp, int serverPort, String userId, int listenPort, BROADCAST_TYPE type) throws IOException, ClassNotFoundException{
		String myIp = InetAddress.getLocalHost().getHostAddress();
		mProcess = new Process(myIp, listenPort, userId);
		switch(type){
		case RELIABLE:
			mBroadcast = new RbBroadcast();
			break;
		case FIFO:
			mBroadcast = new FrbBroadcast();
			break;
		default:
			mBroadcast = new RbBroadcast();
		}
		mBroadcast.init(mProcess, this);
		setupServer(serverIp, serverPort);
	}
	
	private void setupServer(String serverIp, int serverPort) throws UnknownHostException, IOException, ClassNotFoundException{
		//TODO server interaction
		
		//Sends the mProcess to the server and needs to receive a current process list
		Socket serverConnection = new Socket(serverIp, serverPort);
		ObjectOutputStream output = new ObjectOutputStream(serverConnection.getOutputStream());
		output.flush();
		ObjectInputStream input = new ObjectInputStream(serverConnection.getInputStream());
		output.writeObject(mProcess);
		output.flush();
		output.reset();
		
		ArrayList<Process> currproccesses = (ArrayList<Process>)input.readObject();
		for(Process p: currproccesses){
			mBroadcast.addMember(p);
		}
		
		output.close();
		input.close();
		serverConnection.close();
		
	}
	
	public void SendMessage(String message){
		ChatMessage m = new ChatMessage(mProcess, message);
		mBroadcast.broadcast(m);
	}

	@Override
	public void receive(Message m) {
		String name = m.getSource().getID();
		String msg = m.getMessageContents();
		// TODO Print out received message
	}

}
