package part1;

/**
 * Broadcast is 'our' interface that we should rely on I only implemented
 * the interfaces that were supplied because I thought maybe they were
 * required.
 * @author watterso
 *
 */
public interface Broadcast {
	/**
	 * Initializes the broadcast, creating any required class variables
	 * and passing down arguments to its sub broadcasts
	 * 
	 * @param currentProcess Object identifying any broadcasts originating
	 * from the calling application
	 * @param br Target on which the broadcast calls receive() when
	 * conditions have been met to deliver a message
	 */
	public void init(Process currentProcess, BroadcastReceiver br); 
	public void addMember(Process member);
	public void removeMember(Process member);
	public void broadcast(Message m);
	/**
	 * Gives the BroadcastReceiver on which the application should
	 * call receive() when it has constructed an Object implementing
	 * the Message interface
	 * 
	 * @return 'lowest-level' broadcast receiver 
	 */
	public BroadcastReceiver getReceiver();
}
