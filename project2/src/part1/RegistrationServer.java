package part1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class RegistrationServer {
	
	ArrayList<Process> processes;
	
	public void main(String[] args){
		//Get input
	}
	
	public RegistrationServer(int port) throws IOException, ClassNotFoundException{
		ServerSocket server = new ServerSocket(port);
		processes = new ArrayList<Process>();
		
		while(true){
			Socket conn = server.accept(); //assume that the only thing conneting are processes that are registering
			ObjectOutputStream output = new ObjectOutputStream(conn.getOutputStream());
			output.flush(); //always make writer first, and always flush
			ObjectInputStream input = new ObjectInputStream(conn.getInputStream()); //get the process that is registering
			
			Process p = (Process)input.readObject();
			
			output.writeObject(processes);
			output.flush();
			output.reset();
			//need to send out the current list of processes
		}
	}
	
	public void registerProcess(String ip, int port){
		
	}
	
	public void sendNewProcess(){
		
	}
	
	
}
