package part2;

import part1.BroadcastReceiver;
import part1.Message;
import part1.Process;

public interface FIFOReliableBroadcast {
	public void init (Process currentProcess , BroadcastReceiver br ); 
	public void addMember(Process member);
	public void removeMember(Process member);
	public void FIFObroadcast(Message m);
}
