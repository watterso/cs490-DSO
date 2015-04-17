package part1;

import java.util.HashSet;

/**
 * Simplest broadcast class more eloquently called Best Effort Broadcast.
 * This is the lowest level broadcast on which all other processes depend.
 * There is no conditional logic in the receive() function, the BeBroadcast
 * simply immediately delivers by calling receive() on the BroadcastReceiver
 * supplied in the constructor.
 * 
 * @author watterso
 *
 */
public class BeBroadcast implements Broadcast, BroadcastReceiver {
	
	private HashSet<Process> mMembers;
	private BroadcastReceiver mOutputReceiver;
	private Process mCurrentProcess;

	@Override
	public void init(Process currentProcess, BroadcastReceiver br) {
		mMembers = new HashSet<Process>();
		mOutputReceiver = br;
		mCurrentProcess = currentProcess;
	}

	@Override
	public void addMember(Process member) {
		mMembers.add(member);
	}

	@Override
	public void removeMember(Process member) {
		mMembers.remove(member);
	}

	public void broadcast(Message m) {
		for(Process p : mMembers){
			//don't broadcast to yourself, who likes congestion?
			if(p.equals(mCurrentProcess)) continue;
			p.send(m);
		}
	}

	public int getMemberCount(){
		return mMembers.size();
	}

	@Override
	public void receive(Message m) {
		//bebdeliver
		mOutputReceiver.receive(m);
	}

	@Override
	public BroadcastReceiver getReceiver() {
		return this;
	}
}
