package part1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class SingleThreadedChatServer extends Thread{
	public final int heartbeat_rate = 10;


	static volatile ArrayBlockingQueue<Connection> group;		//Group of all connections
	static volatile ConcurrentHashMap<String,String> names;		//List of all the names and their associated <name, ip, port> values


	public static void main(String[] args) throws Exception {
		SingleThreadedChatServer blah = new SingleThreadedChatServer(Integer.parseInt(args[0]));	
	}


	public SingleThreadedChatServer(int port){

		group = new ArrayBlockingQueue<Connection>(10000);
		names = new ConcurrentHashMap<String, String>();

		ServerSocket server;
		try {
			server = new ServerSocket(port);
			this.start();
			while(true){
				Socket newSocket;
				try {
					newSocket = server.accept();

					System.out.println("NEW REQUEST");

					System.out.println("SWEET NIPS");
					ObjectOutputStream writer = new ObjectOutputStream(newSocket.getOutputStream());
					writer.flush();
					System.out.println("SWEET TITS");
					ObjectInputStream reader = new ObjectInputStream(newSocket.getInputStream());

					Connection newMember = new Connection();
					System.out.println("SWEET BUTTS");
					newMember.socket = newSocket;
					newMember.readStream = reader; 
					newMember.writeStream = writer;
					System.out.println("SWEET LIPS");
					newMember.lastHeartbeat = currSeconds();

					System.out.println("Adding connection....");
					boolean k = group.add(newMember);
					System.out.println("Added connection: "+k);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(){
		try{
			while(true){
				if(!group.isEmpty()){
					Connection connection = group.take();
					boolean result = handleMessage(connection);
					if(result){
						group.add(connection);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	boolean handleMessage(Connection connection) throws Exception{  //returns false if the connection was handled poorly and the connection was stopped
		ObjectInputStream reader = connection.readStream;
		if(connection.readStream.available()>0){
			int msgType = connection.readStream.readInt();
			switch(msgType){
			case 1:			//register message
				String name = reader.readUTF();
				if(names.containsKey(name)){
					connection.close();
					return false;
				}else{
					connection.name = name;
					connection.writeStream.writeUTF("connection accepted");
					connection.writeStream.flush();
					String ip = connection.socket.getInetAddress().getHostAddress();
					int port = connection.readStream.readInt();
					names.put(name, "< "+name+" , "+ip+" , "+port+" >");
					System.out.println("added: "+ "< "+name+" , "+ip+" , "+port+" >");
					return true;
				}
			case 2:			//heartbeat message
				if(currSeconds() - connection.lastHeartbeat > heartbeat_rate){
					System.out.println("SAD BUNS");
					connection.close();
					names.remove(connection.name);
					return false;
				}else{
					connection.lastHeartbeat = currSeconds();
					return true;
				}
			case 3:			//get message
				connection.writeStream.writeObject(names.values());
				connection.writeStream.reset();
				return true;
			default:
				connection.close();
				names.remove(connection.name);
				return false;
			}
		}

		if(currSeconds() - connection.lastHeartbeat > heartbeat_rate){
			names.remove(connection.name);
			connection.close();
			return false;
		}

		return true;
	}


	public static int currSeconds(){
		return (int) (System.currentTimeMillis()/1000);
	}
}

class Connection{
	public String name;
	public int port;
	public Socket socket;
	public ObjectInputStream readStream;
	public ObjectOutputStream writeStream;
	public int lastHeartbeat;

	public void close() throws Exception{
		writeStream.flush();
		writeStream.close();
		readStream.close();
		socket.close();
	}

}