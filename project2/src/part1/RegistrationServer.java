package part1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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
			

			//need to send out the current list of processes
			output.writeObject(processes);
			output.flush();
			output.reset();
			
			//register the new process and update everyone elses process list
			registerProcess(p);
			
		}
	}
	
	public void registerProcess(Process p) throws UnknownHostException, IOException{
		for(Process proc : processes){//for each process send the new process before adding it to the list in order to prevent duplicates
			Socket connect = new Socket(proc.getIP(), proc.getPort());
			ObjectOutputStream output = new ObjectOutputStream(connect.getOutputStream());
			output.flush(); //flush after creating the stream
			output.writeInt(0); //tell the chat client that it is getting a new process and not a message
			output.flush();
			output.writeObject(p); //send the process
			output.flush();
			output.reset();
			output.close();
			connect.close();
		}
		processes.add(p); //add the new process to the list
	}
	
	
	
}
