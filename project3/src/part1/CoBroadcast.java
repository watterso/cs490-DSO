package part1;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class CoBroadcast implements Broadcast, BroadcastReceiver {
	private RbBroadcast mRbBroadcast;
	private Process mProcess;
	private BroadcastReceiver mOutputReceiver;
	private SortedSet<Message> mPendingSet;

	@Override
	public void init(Process currentProcess, BroadcastReceiver br) {
		mOutputReceiver = br;
		mProcess = currentProcess;
		mPendingSet = new TreeSet<Message>(new Comparator<Message>() {
			@Override
			public int compare(Message o1, Message o2) {
				return o1.getClock().compareTo(o2.getClock());
			}
		});
		mRbBroadcast = new RbBroadcast();
		mRbBroadcast.init(currentProcess, br);
	}

	@Override
	public void receive(Message m) {
		if(!m.getSource().getID().equals(mProcess.getID())){
			mPendingSet.add(m);
			deliver();
		}else{
			//this came from me!
		}
	}
	
	private void deliver(){
		VectorClock mine = mProcess.getClock();
		Message f = mPendingSet.first();
		while(f != null){
			if(mine.compareTo(f.getClock()) < 0){
				mPendingSet.remove(f);
				mOutputReceiver.receive(f);
				mProcess.incClock(f.getSource().getID());
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
