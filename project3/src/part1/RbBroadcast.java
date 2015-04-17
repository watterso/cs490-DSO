package part1;

import java.util.HashSet;

/**
 * The more useful low level Broadcast fully named Reliable Broadcast.
 * In the receive() function this Broadcast does the gate-keeping logic
 * around rbDeliver as well as implementing the eager algorithm of
 * re-broadcasting all newly received messages. This Broadcast is directly
 * dependent on BeBroadcast.
 * 
 * @author watterso
 *
 */
public class RbBroadcast implements ReliableBroadcast, Broadcast, BroadcastReceiver{

	private HashSet<Integer> mDeliveredMessages;
	private BeBroadcast mBebroadcast;
	private BroadcastReceiver mOutputReceiver;
	private int mHighestReceived;

	@Override
	public void init(Process currentProcess, BroadcastReceiver br) {
		mDeliveredMessages = new HashSet<Integer>();
		mOutputReceiver = br;
		mBebroadcast = new BeBroadcast();
		mBebroadcast.init(currentProcess, this);
		mHighestReceived = 0;
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
			if(messageNumber > mHighestReceived){
				mHighestReceived = messageNumber;
			}
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
		mHighestReceived++;
		m.setMessageNumber(mHighestReceived);
		this.rbroadcast(m);
	}
}
