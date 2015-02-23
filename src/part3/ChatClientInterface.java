package part3;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClientInterface extends Remote{
	public void sendMsg(String msg) throws RemoteException;
	public boolean isChatting()throws RemoteException;
	public void setChatting(ChatClientInterface remote)throws RemoteException;
	public ChatClientInterface getInterface()throws RemoteException;
	
}
