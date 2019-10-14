package client;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import data.Sequence_Number;
import data.Parameter;



/**
 * Sends alive messages to the server and waits for ACK messages. 
 * @author walder daniel - 015153159
 *
 */
public class Alive_Message extends Thread {

	private int myport;
	private int serverport;
	private Client_Sender sender;
	private Sequence_Number seq;
	public AtomicBoolean new_message = new AtomicBoolean(false);
	public AtomicBoolean keep_running = new AtomicBoolean(true);
	public AtomicBoolean send_alive_messages = new AtomicBoolean(true);
	private int counter = 0;
	private int world_id;
	
	
	public Alive_Message(int serverport, int myport, Sequence_Number seq) {
		this.myport = myport;
		this.serverport = serverport;
		this.seq = seq;
	}
	
	/**
	 * Thread method. Sends alive messages to the server. If there is no ACK after x tries, the client will disconnect. 
	 */
	public void run() {
		while(send_alive_messages.get()) {
			try {
				if(counter < Parameter.max_alive_counter) {
					Thread.sleep(5000);
					sender = new Client_Sender(this.serverport, this.myport, seq, world_id);
					sender.setMessage_number(3);
					sender.start();	
					Thread.sleep(5000);
					if(new_message.compareAndSet(true, false)){
						counter = 0;
					}else {
						counter++;
					}
				}else {
					keep_running.set(false);
				}
			} catch (InterruptedException | IOException e) {
					System.out.println("Shutting down alive messages.");
			}
		}
		
	}

	public int getWorld_id() {
		return world_id;
	}

	public void setWorld_id(int world_id) {
		this.world_id = world_id;
	}
}
