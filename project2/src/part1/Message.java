package part1;

public interface Message{
	int getMessageNumber() ;
	void setMessageNumber(int messageNumber);
	String getMessageContents ();
	void setMessageContents(String contents);
	Process getSource();
}
