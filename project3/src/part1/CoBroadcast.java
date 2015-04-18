package part1;

import java.util.ArrayList;
import java.util.Comparator;

/*
 * Look at Lecture 8 Slide 38 for description of CoBroadcast Algo
 */
public class CoBroadcast implements Broadcast, BroadcastReceiver {
	private RbBroadcast mRbBroadcast;
	private Process mProcess;
	private BroadcastReceiver mOutputReceiver;
	private ArrayList<Message> mPendingSet;

	@Override
	public void init(Process currentProcess, BroadcastReceiver br) {
		mOutputReceiver = br;
		mProcess = currentProcess;
		mPendingSet = new ArrayList<Message>();
		mRbBroadcast = new RbBroadcast();
		mRbBroadcast.init(currentProcess, br);
	}

	@Override
	public void receive(Message m) {
		if(!m.getSource().getID().equals(mProcess.getID())){
			mPendingSet.add(m);
			mPendingSet.sort(new Comparator<Message>() {
				@Override
				public int compare(Message o1, Message o2) {
					return o1.getClock().order(o2.getClock());
				}
			});
			deliver();
		}else{
			//this came from me!
		}
	}
	
	private void deliver(){
		VectorClock mine = mProcess.getClock();
		Message f = mPendingSet.get(0);
		while(f != null){
			if(mine.compareTo(f.getClock()) < 0){
				mPendingSet.remove(0);
				mOutputReceiver.receive(f);
				mProcess.incClock(f.getSource().getID());
				f = mPendingSet.get(0);
			}else{
				//they should be in order so if 
				// something is > my current clock stop
				break;
			}
		}
	}
	
	@Override
	public void broadcast(Message m) {
		m.setMessageNumber(mProcess.currClock());
		m.setClock(mProcess.getClock());
		mRbBroadcast.broadcast(m);
		mProcess.incClock();
	}

	@Override
	public void addMember(Process member) {
		//add entry to vector clock?
		mProcess.getClock().init(member.getID());
		mRbBroadcast.addMember(member);
	}

	@Override
	public void removeMember(Process member) {
		mRbBroadcast.removeMember(member);
	}
	
	@Override
	public BroadcastReceiver getReceiver() {
		return mRbBroadcast;
	}

}
