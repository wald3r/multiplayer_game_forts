package data;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import data.Parameter;
/**
* Listener class. Client or Server listens to incoming messages
* @author walder daniel - 015153159
* 
*/
public class Listener extends Thread {

	private final DatagramSocket listener;
	private boolean running = false;
	private DatagramPacket packet;
	private Protocol protocol;
	private Message_Queue q;
	
	public Listener (Message_Queue q, DatagramSocket listener) throws IOException {
		this.listener =listener;
		this.protocol = new Protocol(this.listener);
		this.q = q;
	}
	
	/**
	 * Stop listening
	 */
	public void stop_listener() {
		listener.close();
		running = false;
	}
	
	public void run() {
		running = true;
		while(running) {
			try {
				packet = protocol.receive_message();
				q.messages.put(packet);
			} catch (InterruptedException e) {
				System.out.println("Shutting down listener.");
			}
		}
		listener.close();
		
	}
	
	
}

