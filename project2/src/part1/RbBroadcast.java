package part1;

import java.util.HashSet;

public class RbBroadcast implements ReliableBroadcast, Broadcast, BroadcastReceiver{

	private HashSet<Integer> mDeliveredMessages;
	private BeBroadcast mBebroadcast;
	private BroadcastReceiver mOutputReceiver;

	@Override
	public void init(Process currentProcess, BroadcastReceiver br) {
		mDeliveredMessages = new HashSet<Integer>();
		mOutputReceiver = br;
		mBebroadcast = new BeBroadcast();
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
		int messageNumber = m.getMessageNumber();
		mDeliveredMessages.add(messageNumber);
		this.receive(m);
		mBebroadcast.broadcast(m);
	}

	@Override
	public void receive(Message m) {
		int messageNumber = m.getMessageNumber();
		if(!mDeliveredMessages.contains(messageNumber)){
			mDeliveredMessages.add(messageNumber);
			//rbdeliver
			mOutputReceiver.receive(m);
			mBebroadcast.broadcast(m);
		}
	}

	@Override
	public BroadcastReceiver getReceiver() {
		return mBebroadcast;
	}

	@Override
	public void broadcast(Message m) {
		this.rbroadcast(m);
	}
}
