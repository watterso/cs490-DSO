package part2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import part1.Broadcast;
import part1.BroadcastReceiver;
import part1.Message;
import part1.Process;
import part1.RbBroadcast;

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

	private void addPending(String processId, Message m){
		checkIndexAndInitialize(processId);
		mPendingSet.get(processId).add(m);
	}
	
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
			mOutputReceiver.receive(m);
		}
	}

	@Override
	public void FIFObroadcast(Message m) {
		m.setMessageNumber(mSeqNumber++);
		mRbbroadcast.rbroadcast(m);
	}

	@Override
	public void receive(Message m) {
		String processId = m.getSource().getID();
		int seqNumber = m.getMessageNumber();
		addPending(processId, m);
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
