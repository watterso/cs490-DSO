package part1;

import java.util.HashSet;

public class BestEffortBroadcast implements Broadcast {
	private HashSet<Process> mMembers;
	private BroadcastReceiver bebReceiver;

	@Override
	public void init(Process currentProcess, BroadcastReceiver br) {
		mMembers = new HashSet<Process>();
		bebReceiver = br;
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
			p.send(m);
		}
	}

}
