package part3;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatClient extends UnicastRemoteObject implements ChatClientInterface{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	public ChatClient() throws RemoteException {
		super();
	}

	@Override
	public void sendMsg(String msg) {
		System.out.println(msg);
	}

}
