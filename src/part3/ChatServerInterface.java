package part3;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface ChatServerInterface extends Remote{
	public String getGroup() throws RemoteException;
	public boolean register(String name, ChatClientInterface client) throws RemoteException;
	public ChatClientInterface getClient(String name) throws RemoteException;
	public void heartbeat(String name) throws RemoteException;
}