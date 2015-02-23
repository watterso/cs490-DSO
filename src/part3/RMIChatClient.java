package part3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.Collection;
import java.util.Scanner;

public class RMIChatClient extends Thread{
	public static void main(String[] args) throws IOException, NotBoundException{
		System.out.println("enter name");
		Scanner input = new Scanner(System.in);
		String name = input.nextLine();
		RMIChatClient blah = new RMIChatClient(name);
	}
	
	ServerSocket requestSocket;
	Socket chatSocket;
	boolean chatting;
	ChatClient remoteClient;
	
	public RMIChatClient(String name) throws IOException, NotBoundException{
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		chatting = false;
		
		ChatClient mClient = new ChatClient();
		ChatServerInterface server = (ChatServerInterface)Naming.lookup("rmi://localhost/le_chat");
		boolean reg_success = server.register(name, mClient);
		if(!reg_success){
			System.out.println("That name is already taken.");
			return;
		}
		//TODO thread to do heartbeats
		this.start();
		System.out.println("Start Chatting!");
		while(true){
			while(!chatting){
				if(input.ready()){
					String line = input.readLine();
					if(line.equals("getgroup")){
						/*Collection<String> group = server.getGroup();
						for(String client : group){
							System.out.println(client);
						}*/
						System.out.println(server.getGroup());
					}else if(line.equals("connectto")){
						System.out.print("Enter client's name: ");
						String remote_name = input.readLine();
						remoteClient =  (ChatClient) server.getClient(remote_name);
						if(remoteClient == null){
							System.out.println("Invalid name.");
						}else{
							chatting = true;
							System.out.println("Now Chatting with "+remote_name);
						}

					}else{
						System.out.println("Invalid command");
					}
				}
			}
			while(chatting){
				System.out.println(chatting);
				remoteClient.sendMsg(input.readLine());
			}
		}
	}
	public void run(){

		while(!chatting){ //Check whether we connect to someone else, or if they connect to us
			try {
				//chatting = true;
			} catch (Exception e) {
				//Do nothing on timeout
			}
		}
	}
}
