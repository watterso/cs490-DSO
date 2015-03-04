package part1;

public interface ReliableBroadcast {
	public void init(Process currentProcess, BroadcastReceiver br); 
	public void addMember(Process member);
	public void removeMember(Process member);
	public void rbroadcast (Message m);
}
