package part1;

public class RbBroadcast implements ReliableBroadcast, BroadcastReceiver{

	private BeBroadcast mBebroadcast;
	private BroadcastReceiver mOutputReceiver;
	
	@Override
	public void init(Process currentProcess, BroadcastReceiver br) {
		mBebroadcast = new BeBroadcast();
		mOutputReceiver = br;
		mBebroadcast.init(currentProcess, this);
	}

	@Override
	public void addMember(Process member) {
		mBebroadcast.addMember(member);
	}

	@Override
	public void removeMember(Process member) {
		mBebroadcast.removeMember(member);
	}

	@Override
	public void rbroadcast(Message m) {
		mBebroadcast.broadcast(m);
	}

	@Override
	public void receive(Message m) {
		rbroadcast(m);
		//rbdeliver
		mOutputReceiver.receive(m);
	}

}
