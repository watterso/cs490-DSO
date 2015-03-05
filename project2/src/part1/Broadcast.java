package part1;


public interface Broadcast {
	public void init(Process currentProcess, BroadcastReceiver br); 
	public void addMember(Process member);
	public void removeMember(Process member);
	public BroadcastReceiver getReceiver();
}
