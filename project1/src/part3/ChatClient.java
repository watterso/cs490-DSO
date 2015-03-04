package part3;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatClient extends UnicastRemoteObject implements ChatClientInterface{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private ChatClientInterface blah;
	private boolean ischatting;
	public ChatClient() throws RemoteException {
		super();
		blah = null;
		ischatting = false;
	}

	@Override
	public void sendMsg(String msg) throws RemoteException{
		System.out.println(msg);
	}

	@Override
	public boolean isChatting() throws RemoteException{
		// TODO Auto-generated method stub
		
		return ischatting;
	}

	@Override
	public void setChatting(ChatClientInterface remote) throws RemoteException{
		// TODO Auto-generated method stub
		blah = remote;
		ischatting=true;
		
		
	}
	
	public ChatClientInterface getInterface()throws RemoteException{
		return blah;
	}

}
