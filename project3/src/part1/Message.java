package part1;

import java.io.Serializable;

public interface Message extends Serializable{
	int getMessageNumber() ;
	void setMessageNumber(int messageNumber);
	String getMessageContents ();
	void setMessageContents(String contents);
	Process getSource();
	VectorClock getClock();
	void setClock(VectorClock v);
}
