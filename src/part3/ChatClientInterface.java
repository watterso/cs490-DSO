package part3;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClientInterface extends Remote{
	public void sendMsg(String msg) throws RemoteException;
}
