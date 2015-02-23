package part3;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.HashMap;

public class RMIChatServer extends UnicastRemoteObject implements ChatServerInterface{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String [] args){
		System.out.println("server started");
		try {
			RMIChatServer server = new RMIChatServer();
			Naming.rebind("rmi://localhost/le_chat", server);
			//TODO call server.prune to catch heartbeats fails, alert clients of deregister?
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	HashMap<String, ChatClientInterface> names;
	HashMap<String,Integer> heartbeatVals;
	static final int HEART_BEAT_TIMEOUT = 10;

	protected RMIChatServer() throws RemoteException {
		super();
		names = new HashMap<String, ChatClientInterface>();
		heartbeatVals = new HashMap<String, Integer>();
	}

	@Override
	public String getGroup() {
		Collection<String>temp = names.keySet();
		StringBuilder builder = new StringBuilder();
		for(String t : temp){
			builder.append(t);
			builder.append("\n");
		}
		return builder.toString();
	}

	@Override
	public boolean register(String name, ChatClientInterface client) {
		if(names.containsKey(name)){
			return false;
		}else{
			names.put(name, client);
			heartbeatVals.put(name, currSeconds());
			System.out.println(name);
			return true;
		}
	}

	@Override
	public void heartbeat(String name) {
		if(names.containsKey(name)){
			heartbeatVals.put(name, currSeconds());
		}
	}

	public void prune(){
		for(String k : heartbeatVals.keySet()){
			if(currSeconds() - heartbeatVals.get(k) > HEART_BEAT_TIMEOUT){
				heartbeatVals.remove(k);
				names.remove(k);
			}
		}
	}

	public static int currSeconds(){
		return (int) (System.currentTimeMillis()/1000);
	}

	@Override
	public ChatClientInterface getClient(String name) {
		return names.get(name);
	}

}
