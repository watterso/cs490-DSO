package part1;

import java.net.InetAddress;
import java.net.UnknownHostException;

import part2.FrbBroadcast;

public class ChatClient implements BroadcastReceiver {
	
	public enum BROADCAST_TYPE{
		RELIABLE,
		FIFO
	};
	
	private Process mProcess;
	private Broadcast mBroadcast;
	
	public ChatClient(String serverIp, int serverPort, String userId, int listenPort) throws UnknownHostException{
		String myIp = InetAddress.getLocalHost().getHostAddress();
		mProcess = new Process(myIp, listenPort, userId);
		mBroadcast = new RbBroadcast();
		mBroadcast.init(mProcess, this);
		setupServer(serverIp, serverPort);
	}
	
	public ChatClient(String serverIp, int serverPort, String userId, int listenPort, BROADCAST_TYPE type) throws UnknownHostException{
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
	
	private void setupServer(String serverIp, int serverPort){
		//TODO server interaction
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
