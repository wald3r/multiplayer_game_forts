package server;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import data.Sequence_Number;
import data.Parameters;

/**
* Special settings class to send data to the client.
* @author walder daniel - 015153159
* 
*/
public class Special_Settings extends Thread{
	
	private int myport;
	private int serverport;
	private Server_Sender sender;
	private Sequence_Number seq;	
	public AtomicBoolean keep_running = new AtomicBoolean(true);
	private int id;
	
	
	public Special_Settings(int serverport, int myport, Sequence_Number seq, int id) {
		this.myport = myport;
		this.serverport = serverport;
		this.seq = seq;
		this.id = id;
	}
	
	/**
	 * Keeps sending extra data to a client
	 */
	public void run() {
		while(keep_running.get()) {
			try {
				Thread.sleep(Parameters.special_settings_waiting_time);
				sender = new Server_Sender(this.serverport, this.myport, seq, id);
				sender.setMessage_number(5);
				sender.start();	
					
			} catch (InterruptedException | IOException e) {
				System.out.println("Shutting down special settings.");
			}
		}
		
	}

	public int getServerport() {
		return serverport;
	}

	public void setServerport(int serverport) {
		this.serverport = serverport;
	}


}
