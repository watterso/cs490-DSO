package part2;

import java.util.Comparator;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import part1.Broadcast;
import part1.BroadcastReceiver;
import part1.Message;
import part1.Process;
import part1.RbBroadcast;

/**
 * A more complicated Broadcast more eloquently named FIFO Reliable Broadcast.
 * The logic for maintaining the FIFO ordering of messages from each specific
 * process is located in receive(), but is split up into smaller private 
 * functions readability sake. See those functions for further documentation.
 * A key assumption of the pending set is that because a Message will only be
 * rbDelivered once, after a Message is frbDelivered, it is removed from the
 * pending set. This Broadcast is directly dependent on RbBroadcast.
 * 
 * @author watterso
 *
 */

public class FrbBroadcast implements FIFOReliableBroadcast, Broadcast, BroadcastReceiver {

	private RbBroadcast mRbbroadcast;
	private BroadcastReceiver mOutputReceiver;
	private HashMap<String,SortedSet<Message>> mPendingSet;
	private int mSeqNumber;

	@Override
	public void init(Process currentProcess, BroadcastReceiver br) {
		mOutputReceiver = br;
		mRbbroadcast = new RbBroadcast();
		mRbbroadcast.init(currentProcess, this);
		mSeqNumber = 0;
		mPendingSet = new HashMap<String,SortedSet<Message>>();
	}
	
	/**
	 * Checks to see if a pending set is already created for Messages originating 
	 * from a process identified by processId.
	 * @param processId	Id of process for which to verify the pending set exists
	 */
	private void checkIndexAndInitialize(String processId){
		if(mPendingSet.get(processId) == null){
			mPendingSet.put(processId, new TreeSet<Message>(new Comparator<Message>() {
				@Override
				public int compare(Message o1, Message o2) {
					return o1.getMessageNumber() - o2.getMessageNumber();
				}
			}));
		}
	}

	/**
	 * Performs the gate keeper logic for delivering a Message
	 * of sequence number seqNumber from Process with id processId.
	 * Before a Message can be delivered, all prior messages must be pending
	 * to be delivered first.
	 * @param processId Id of Process for which to verify gate keeper logic
	 * @param seqNumber Sequence number of Message for which to verify gate keeper logic
	 * @return true if a Message is ready to be delivered, false if not.
	 */
	private boolean allPriorMessagesPending(String processId, int seqNumber){
		if(mPendingSet.containsKey(processId)){
			SortedSet<Message> priors = mPendingSet.get(processId);
			int val = priors.first().getMessageNumber();
			for(Message m : priors){
				if(val != m.getMessageNumber()){
					return false;
				}else{
					val++;
				}
			}
			//if there are higher values, that is okay
			if(val >= seqNumber){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Adds supplied Message to the pending set of process identified by
	 * processId
	 * @param processId Id of Process to whom's pending set the Message will be added
	 * @param m	Message to be added to a pending set.
	 */
	private void addToPending(String processId, Message m){
		checkIndexAndInitialize(processId);
		mPendingSet.get(processId).add(m);
	}

	/**
	 * Delivers Messages in the pending set for the Process identified by
	 * processId up until and including the message with sequence number
	 * seqNumber.
	 * @param processId Process whose pending set is targeted for delivery.
	 * @param seqNumber Inclusive upper limit on which messages to deliver.
	 */
	private void deliverUntil(String processId, int seqNumber) {
		if(mPendingSet.containsKey(processId)){
			SortedSet<Message> priors = mPendingSet.get(processId);
			Message m = priors.first();
			while(m.getMessageNumber() < seqNumber){
				priors.remove(m);
				mOutputReceiver.receive(m);
				m = priors.first();
			}
			// remaining set is seqNumber and higher, deliver seqNumber
			m = priors.first();
			priors.remove(m);
			//frbDeliver
			mOutputReceiver.receive(m);
		}
	}

	@Override
	public void FIFObroadcast(Message m) {
		//m.setMessageNumber(mSeqNumber++);
		mRbbroadcast.rbroadcast(m);
	}

	@Override
	public void receive(Message m) {
		String processId = m.getSource().getID();
		int seqNumber = m.getMessageNumber();
		addToPending(processId, m);
		if(allPriorMessagesPending(processId, seqNumber)){
			deliverUntil(processId, seqNumber);
		}
	}

	@Override
	public BroadcastReceiver getReceiver() {
		return mRbbroadcast;
	}

	@Override
	public void addMember(Process member) {
		mRbbroadcast.addMember(member);
	}

	@Override
	public void removeMember(Process member) {
		mRbbroadcast.addMember(member);
	}

	@Override
	public void broadcast(Message m) {
		this.FIFObroadcast(m);
	}
}
