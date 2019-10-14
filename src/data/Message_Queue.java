package data;

import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
* BlockingQueue to ensure thread safe communication.
* @author walder daniel - 015153159
* 
*/
public class Message_Queue {

	public BlockingQueue<DatagramPacket> messages = new LinkedBlockingQueue<DatagramPacket>();

	public BlockingQueue<DatagramPacket> getQueue() {
		return messages;
	}

	public void setQueue(BlockingQueue<DatagramPacket> queue) {
		this.messages = queue;
	}
	
	
}
