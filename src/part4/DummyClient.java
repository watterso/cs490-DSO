package part4;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class DummyClient {
	public static final int DUMMY_POPULATION = 100000;
	public static long[][] timestamps = new long[DUMMY_POPULATION][2];	//start and end timestamps of requests
	public static void main(String [] args) throws IOException{
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		String base_name = args[2];
		for(int i =0; i<DUMMY_POPULATION; i++){
			dummy_register(ip, port, base_name, i);
		}
		calc();
	}
	public static void calc(){
		long[]latency = new long[DUMMY_POPULATION];
		HashMap<Long,Integer> through = new HashMap<Long,Integer>();
		double lat_mean = 0;
		for(int i =0; i<DUMMY_POPULATION; i++){
			latency[i] = timestamps[i][1]-timestamps[i][0];
			lat_mean += latency[i];
			long seconds = (int)((timestamps[i][1]+latency[i]/2)/1000);
			int new_val = through.getOrDefault(seconds, 0)+1;
			through.put(seconds, new_val);
		}
		lat_mean = lat_mean/DUMMY_POPULATION;
		double through_mean = 0;
		for(int x : through.values()){
			through_mean += x;
		}
		through_mean = through_mean/through.size();
		double lat_dev = 0;
		for(int i =0; i<DUMMY_POPULATION; i++){
			lat_dev += Math.pow(latency[i]-lat_mean, 2);
		}
		lat_dev = Math.sqrt(lat_dev/DUMMY_POPULATION);
		double through_dev = 0;
		for(int x : through.values()){
			through_dev += Math.pow(x-through_mean, 2);
		}
		through_dev = Math.sqrt(through_dev/DUMMY_POPULATION);
		System.out.println("---Latency---");
		System.out.println("  mean: "+lat_mean);
		System.out.println(" std dev: "+lat_dev);
		System.out.println("---Throughput---");
		System.out.println("  mean: "+through_mean);
		System.out.println(" std dev: "+through_dev);

	}

	public static void dummy_register(String ip, int serverport, String name_base, int index) throws IOException{
		Socket socket = new Socket(ip,serverport);
		ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

		output.writeInt(1);    //Specifying its a register message
		output.writeUTF(name_base+index); //send the name
		output.writeInt(1234);
		output.flush();
		timestamps[index][0] = System.currentTimeMillis();
		String response = input.readUTF();
		timestamps[index][1] = System.currentTimeMillis();
		if(response.compareTo("connection accepted")!=0){
			socket.close();
			throw new IOException();
		}
		socket.close();
	}
}
