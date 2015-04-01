package part1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import part2.FrbBroadcast;

public class ChatClient extends Thread implements BroadcastReceiver {
	
	public enum BROADCAST_TYPE{
		RELIABLE,
		FIFO
	};
	
	private Process mProcess;
	private Broadcast mBroadcast;
	
	ServerSocket mSocket;
	
	int curmesgnum=0;
	
	public static void main(String args[]){
		if(args.length<5){
			System.out.println("Usage: java ChatClient ServerIP ServerPort ClientID ClientPort Broadcasttype");
			System.out.println("The broadcast types are 0 - reliable, 1 - FIFO");
			System.exit(0);
		}
		try {
			ChatClient client = new ChatClient(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4])==1?BROADCAST_TYPE.FIFO:BROADCAST_TYPE.RELIABLE);
			Scanner input = new Scanner(System.in);
			while(true){
			
					String msg = input.nextLine();
					System.out.println("Sending msg: "+msg);
					client.SendMessage(msg);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
	}
	
	
	
	public ChatClient(String serverIp, int serverPort, String userId, int listenPort) throws IOException, ClassNotFoundException{
		String myIp = InetAddress.getLocalHost().getHostAddress();
		mProcess = new Process(myIp, listenPort, userId);
		mSocket = new ServerSocket(listenPort);
		mBroadcast = new RbBroadcast();
		mBroadcast.init(mProcess, this);
		setupServer(serverIp, serverPort);

		this.start();
	}
	
	public void run(){
		try {
			while(true){
				Socket sock = mSocket.accept();
				ObjectInputStream input = new ObjectInputStream(sock.getInputStream());
				int mtype = input.readInt();
				if(mtype == 1){ //next object is a message
					Message m = (Message)input.readObject();
					mBroadcast.getReceiver().receive(m);
				}else if(mtype == 0){ //next object is a process to be added to the broadcast list
					Process p = (Process)input.readObject();
					mBroadcast.addMember(p);
					System.out.println("Added Process: "+p.getID()+" : "+p.getIP()+" : "+p.getPort()+" added");
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
		mSocket = new ServerSocket(listenPort);
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
		this.start();
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
			System.out.println("Process: "+p.getID()+" : "+p.getIP()+" : "+p.getPort()+" added");
		}
		
		output.close();
		input.close();
		serverConnection.close();
		System.out.println("I connected");
		
	}
	
	public void SendMessage(String message){
		curmesgnum++;
		ChatMessage m = new ChatMessage(mProcess, message,curmesgnum);
		mBroadcast.broadcast(m);
	}

	@Override
	public void receive(Message m) {
		String name = m.getSource().getID();
		String msg = m.getMessageContents();
		curmesgnum = m.getMessageNumber();
		// TODO Print out received message
		
		System.out.println(System.currentTimeMillis()+" : "+name+": "+msg);
	}

}
