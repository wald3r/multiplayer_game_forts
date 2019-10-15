package client;

import java.io.IOException;

import data.Fortress;
import data.Sequence_Number;
import data.Parameters;

/**
 * Improve mining class. Improve the mining level of a fortress
 * @author walder - 015153159
 *
 */
public class Improve_Mining extends Thread {
	
	private Fortress fort;
	private Client_Sender sender;
	private Sequence_Number seq;
	private int serverport;
	private int myport;
	private int world_id;
	
	public Improve_Mining(Fortress fort, int serverport, int myport, Sequence_Number seq) {
		this.fort = fort;
		this.myport = myport;
		this.serverport = serverport;
		this.seq = seq;
	}
	
	/**
	 * Improves mining level if there are enough resources stored and some time has passed. Server gets informed afterwards. 
	 */
	public void run() {
		if(fort.getResources() > (Parameters.improve_mining_costs*fort.getMining_level())) {			
			try {
				fort.setResources(fort.getResources()-(Parameters.improve_mining_costs*fort.getMining_level()));
				Thread.sleep(Parameters.improve_mining_time);
				fort.setMining_level(fort.getMining_level()+1);
				sender = new Client_Sender(this.serverport, this.myport, this.fort, this.seq, world_id);
				sender.setMessage_number(2);
				sender.start();	
			} catch (InterruptedException | IOException e) {
				System.out.println("Shutting down improve mining.");
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
