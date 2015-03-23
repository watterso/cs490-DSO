package part1;

public class ChatMessage implements Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3374003399602388952L;
	private Process mSource;
	private int mMessageNumber;
	private String mContent;
	
	public ChatMessage(Process src) {
		mSource = src;
		mContent = "";
		mMessageNumber = 0;
	}
	
	public ChatMessage(Process src, String content) {
		mSource = src;
		mContent = content;
		mMessageNumber = 0;
	}
	
	public ChatMessage(Process src, String content, int msgNumber) {
		mSource = src;
		mContent = content;
		mMessageNumber = msgNumber;
	}

	@Override
	public int getMessageNumber() {
		return mMessageNumber;
	}

	@Override
	public void setMessageNumber(int messageNumber) {
		mMessageNumber = messageNumber;
	}

	@Override
	public String getMessageContents() {
		return mContent;
	}

	@Override
	public void setMessageContents(String contents) {
		mContent = contents;
	}
	
	public Process getSource(){
		return mSource;
	}

}
