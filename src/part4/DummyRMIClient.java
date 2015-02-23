package part4;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import part3.ChatClient;
import part3.ChatServerInterface;

public class DummyRMIClient {
	public static final int DUMMY_POPULATION = 100000;
	public static long[][] timestamps = new long[DUMMY_POPULATION][2];	//start and end timestamps of requests
	
	public static void main(String [] args) throws IOException, NotBoundException{
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		String base_name = args[2];
		for(int i =0; i<DUMMY_POPULATION; i++){
			dummy_register(base_name, i);
		}
		//DummyClient.calc();
	}
	
	public static void dummy_register(String name_base, int index) throws MalformedURLException, RemoteException, NotBoundException, UnknownHostException{
		ChatServerInterface server = (ChatServerInterface)Naming.lookup("rmi://localhost/le_chat");
		timestamps[index][0] = System.currentTimeMillis();
		ChatClient client = new ChatClient();
		//Register client
		server.register(name_base+index, client);
		//Get latency of simplest command
		server.getGroup();
		timestamps[index][1] = System.currentTimeMillis();
	}
	
}
