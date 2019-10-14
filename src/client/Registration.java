package client;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import data.Sequence_Number;
import data.Parameter;

/**
* Registration class of the client. Tries to register the client to the server
* @author walder daniel - 015153159
* 
*/
public class Registration extends Thread {

	
	private Client_Sender sender;
	private Client_Data data;
	private int serverport;
	private int myport; 
	private Sequence_Number seq;
	public AtomicBoolean received = new AtomicBoolean(false);
	
	
	public Registration(int serverport, int myport, Sequence_Number seq, Client_Data data) {
		this.serverport = serverport;
		this.myport = myport;
		this.seq = seq;
		this.data = data;
	}
	
	/**
	 * Keeps sending a specific amount of time a registration message till an answer arrives. 
	 */
	public void run() {
		try {
			data.registered.set(false);
			int x = 0;
			while(x < Parameter.registration_tries) {
				sender = new Client_Sender(serverport, myport, seq);
				sender.start();
				Thread.sleep(Parameter.registration_waiting_time);
				if(received.get()) {
					break;
				}else {
					System.out.println("Couldn't connect yet!");
					x++;
				}
			}
		} catch (IOException | InterruptedException e) {
			System.out.println("Shutting down registration");
		}
	}

	
}
