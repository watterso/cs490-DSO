package part1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Scanner;

public class ChatClient extends Thread{

	public static void main(String[] args) throws UnknownHostException, IOException {
		if(args.length<2){
			Scanner input = new Scanner(System.in);
			String serverip = input.nextLine();
			String name = input.nextLine();
			int port  = Integer.parseInt(input.nextLine());
			ChatClient blah = new ChatClient(name, serverip, port, 4269, 10);
			blah.connect();
			blah.chat();
		}else{
			int port = Integer.parseInt(args[1]);
			ChatClient blah = new ChatClient(args[0], "localhost", port, 4269, 10);	
			blah.connect();
			blah.chat();
		}

	}
	public void connect(){
		try {
			centralServer = new ServerConnection(mServerIp, mName, mPort, mServerPort, heartbeat_rate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void chat() throws IOException{
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		this.start();
		System.out.println("Start chatting!");

		while(true){
			while(!chatting){
				if(input.ready()){
					String line = input.readLine();
					if(line.equals("getgroup")){
						Collection<String> group = centralServer.getGroup();
						for(String client : group){
							System.out.println(client);
						}
					}else if(line.equals("connectto")){
						System.out.print("Enter client's ip: ");
						String ip = input.readLine();
						System.out.print("Enter client's port: ");
						int clientport = Integer.parseInt(input.readLine());
						System.out.println(ip + " : "+clientport);

						//Do connect stuff here
						try{
							chatSocket = new Socket(ip,clientport);
						}catch(Exception e){
							System.out.println("Could not connect to host");
							continue;
						}

						chatting = true;

					}else{
						System.out.println("Invalid command");
					}
				}
			}
			BufferedWriter output=new BufferedWriter(new OutputStreamWriter(chatSocket.getOutputStream()));
			while(chatting){
				output.write(input.readLine());
				output.newLine();
				output.flush();
			}
		}
	}

	ServerConnection centralServer;
	int mPort;
	String mName;
	String mServerIp;
	int mServerPort;
	int heartbeat_rate;
	ServerSocket requestSocket;
	Socket chatSocket;
	boolean chatting;

	public ChatClient(String name, String serverip, int port, int serverport, int heartbeat_rate) throws UnknownHostException, IOException{
		chatting = false;

		requestSocket = new ServerSocket(port);
		requestSocket.setSoTimeout(1);
		//socket to listen for chat requests on
		
		mName =name;
		mServerIp = serverip;
		mPort = port;
		mServerPort = serverport;
		this.heartbeat_rate = heartbeat_rate;
		
	}

	public void run(){

		while(!chatting){ //Check whether we connect to someone else, or if they connect to us
			try {
				chatSocket = requestSocket.accept();
				chatting = true;
			} catch (Exception e) {
				//Do nothing on timeout
			}
		}


		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
			chatting = true;
			System.out.println("Someone connected!");
			while(chatSocket.isConnected()){
				String inc_str = input.readLine();
				if(inc_str == null){
					chatting = false;
					break;
				}
				System.out.println(inc_str);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

class ServerConnection extends Thread{
	Socket socket;
	ObjectInputStream input;
	ObjectOutputStream output;
	int heartbeat_rate;


	public ServerConnection(String ip, String name,  int clientport, int serverport, int heartbeat_rate) throws UnknownHostException, IOException{
		socket = new Socket(ip,serverport);
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());

		output.writeInt(1);    //Specifying its a register message
		output.writeUTF(name); //send the name
		output.writeInt(clientport);
		output.flush();

		String response = input.readUTF();
		if(response.compareTo("connection accepted")!=0){
			System.out.println("Connection refused");
			throw new IOException();
		}
		System.out.println("Connected to server!");
		Collection<String> group = this.getGroup();
		for(String client : group){
			System.out.println(client);
		}
		this.start();

	}

	public void run(){
		try {
			while(true){
				synchronized(this){
					output.writeInt(2);
					output.flush();
				}
				Thread.sleep(heartbeat_rate*1000);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Collection<String> getGroup(){
		synchronized(this){
			try{
				output.writeInt(3);
				output.flush();
				Object ret;
				ret = input.readObject();
				if(ret instanceof Collection<?>){
					return (Collection<String>) ret;
				}else{
					return null;
				}
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}

		}
	}

}