package part1;

import java.util.HashSet;

public class BeBroadcast implements Broadcast, BroadcastReceiver {
	private HashSet<Process> mMembers;
	private HashSet<Integer> mDeliveredMessages;
	private BroadcastReceiver mOutputReceiver;
	private Process mCurrentProcess;

	@Override
	public void init(Process currentProcess, BroadcastReceiver br) {
		mMembers = new HashSet<Process>();
		mDeliveredMessages = new HashSet<Integer>();
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

	@Override
	public void broadcast(Message m) {
		for(Process p : mMembers){
			//don't broadcast to yourself who likes congestion?
			if(p.equals(mCurrentProcess)) continue;
			p.send(m);
		}
	}
	
	public int getMemberCount(){
		return mMembers.size();
	}

	@Override
	public void receive(Message m) {
		int messageNumber = m.getMessageNumber();
		if(!mDeliveredMessages.contains(messageNumber)){
			mDeliveredMessages.add(messageNumber);
			//bebdeliver
			mOutputReceiver.receive(m);
		}
	}
}
