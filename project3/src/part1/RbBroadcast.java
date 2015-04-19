package part1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

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

	private HashSet<VectorClock> mDeliveredMessages;
	private BeBroadcast mBebroadcast;
	private BroadcastReceiver mOutputReceiver;

	@Override
	public void init(Process currentProcess, BroadcastReceiver br) {
		mDeliveredMessages = new HashSet<VectorClock>();
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
		VectorClock msgClk = m.getClock();
		mDeliveredMessages.add(msgClk);
		this.receive(m);

		mBebroadcast.broadcast(m);
	}

	@Override
	public void receive(Message m) {
		VectorClock msgClk = m.getClock();
		Iterator<VectorClock> it = mDeliveredMessages.iterator();
		boolean b=false;
		while(it.hasNext()){
			if(it.next().compareTo(msgClk)==0){
				b = true;
			}
		}
		//if(!mDeliveredMessages.contains(msgClk)){
		if(!b){
			mDeliveredMessages.add(msgClk);
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
