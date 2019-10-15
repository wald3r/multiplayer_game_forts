package data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
* BlockingQueue to ensure thread safe communication.
* @author walder daniel - 015153159
* 
*/
public class Message_Queue <T> {

	public BlockingQueue<T> messages = new LinkedBlockingQueue<T>();

	public BlockingQueue<T> getQueue() {
		return messages;
	}

	public void setQueue(BlockingQueue<T> queue) {
		this.messages = queue;
	}
	
	
}
