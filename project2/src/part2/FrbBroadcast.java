package part2;

import part1.BroadcastReceiver;
import part1.Message;
import part1.Process;
import part1.RbBroadcast;

public class FrbBroadcast implements FIFOReliableBroadcast, BroadcastReceiver {
	
	private RbBroadcast mRbbroadcast;
	private BroadcastReceiver mOutputReceiver;
	private int mSeqNumber;

	@Override
	public void init(Process currentProcess, BroadcastReceiver br) {
		mOutputReceiver = br;
		mRbbroadcast = new RbBroadcast();
		mRbbroadcast.init(currentProcess, this);
		mSeqNumber = 0;
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
	public void FIFObroadcast(Message m) {
		// TODO Auto-generated method stub
	}

	@Override
	public void receive(Message m) {
		// TODO Auto-generated method stub
	}
}
