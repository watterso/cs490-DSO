package part2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultithreadedChatServer{
	public final static int heartbeat_rate = 10;
	public final int NUM_THREADS = 3;
	
	
	static volatile ArrayBlockingQueue<Connection> group;		//Group of all connections
	static volatile ConcurrentHashMap<String,String> names;		//List of all the names and their associated <name, ip, port> values
	
	
	public static void main(String[] args) throws Exception {
		MultithreadedChatServer blah = new MultithreadedChatServer(Integer.parseInt(args[0]));	
	}
	
	
	public MultithreadedChatServer(int port){
		
		group = new ArrayBlockingQueue<Connection>(10000);
		names = new ConcurrentHashMap<String, String>();
		
		
		ServerSocket server;
		try {
			server = new ServerSocket(port);
			ThreadPoolExecutor pool = new ThreadPoolExecutor(NUM_THREADS,NUM_THREADS,(long)0,TimeUnit.SECONDS, (BlockingQueue)group);
			pool.prestartAllCoreThreads();
			
			while(true){
				Socket newSocket = server.accept();
				ObjectOutputStream writer = new ObjectOutputStream(newSocket.getOutputStream());
				writer.flush();
				ObjectInputStream reader = new ObjectInputStream(newSocket.getInputStream());
				
				Connection newMember = new Connection();
				newMember.socket = newSocket;
				newMember.readStream = reader; 
				newMember.writeStream = writer;
				newMember.lastHeartbeat = currSeconds();
				newMember.parent = this;
				
				group.add(newMember);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	
	static boolean handleMessage(Connection connection) throws Exception{  //returns false if the connection was handled poorly and the connection was stopped
		ObjectInputStream reader = connection.readStream;
		
		if(connection.readStream.available()>0){
			int msgType = connection.readStream.readInt();
			switch(msgType){
			case 1:			//register message
				String name = reader.readUTF();
				if(names.containsKey(name)){
					connection.writeStream.writeUTF("connection refused");
					connection.close();
					return false;
				}else{
					connection.name = name;
					connection.writeStream.writeUTF("connection accepted");
					connection.writeStream.flush();
					String ip = connection.socket.getInetAddress().getHostAddress();
					int port = reader.readInt();
					names.put(name, "< "+name+" , "+ip+" , "+port+" >");
					return true;
				}
			case 2:			//heartbeat message
				if(currSeconds() - connection.lastHeartbeat > heartbeat_rate){
					connection.close();
					names.remove(connection.name);
					return false;
				}else{
					connection.lastHeartbeat = currSeconds();
					return true;
				}
			case 3:			//get message
				connection.writeStream.writeObject(names.values());
				return true;
			default:
				connection.close();
				names.remove(connection.name);
				return false;
			}
		}
		
		if(currSeconds() - connection.lastHeartbeat > heartbeat_rate){
			connection.close();
			names.remove(connection.name);
			return false;
		}
		
		return true;
	}
	
	
	public static int currSeconds(){
		return (int) (System.currentTimeMillis()/1000);
	}
}

class Connection implements Runnable{
	public String name;
	public Socket socket;
	public ObjectInputStream readStream;
	public ObjectOutputStream writeStream;
	public int lastHeartbeat;
	public MultithreadedChatServer parent;
	
	public void close() throws Exception{
		writeStream.flush();
		writeStream.close();
		readStream.close();
		socket.close();
	}

	public void run() {
		try{
				Connection connection = this;
				boolean result = parent.handleMessage(connection);
				if(result){
					parent.group.add(connection);
				}else{
					//dont add connection back to the queue
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}