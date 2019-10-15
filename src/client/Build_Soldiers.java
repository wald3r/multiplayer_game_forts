package client;

import java.io.IOException;

import data.Fortress;
import data.Sequence_Number;
import data.Parameters;


/**
 * Informs server of an attack after some time has passed. 
 * @author walder - 015153159
 *
 */
public class Build_Soldiers extends Thread {
	
	private Fortress fort;
	private int serverport;
	private int myport;
	private Client_Sender sender;
	private int number;
	private int amount;
	private Sequence_Number seq;
	private int world_id;
		
		
	public Build_Soldiers(Fortress fort, int serverport, int myport, int amount, int number, Sequence_Number seq) {
			this.fort = fort;
			this.myport = myport;
			this.serverport = serverport;
			this.number = number;
			this.amount = amount;
			this.seq = seq;
	}
		
	/**
	 * Produces new soldiers. After production time the server gets informed for every new soldier. 
	 */
	public void run() {
		int x = 0;
		fort.setResources(fort.getResources()-(Parameters.soldier_production_costs*amount));
		while(x < amount) {
			x++;
			try {
				Thread.sleep(Parameters.soldier_production_time);
				if(number == 1) {
					fort.setAttacking_soldiers(fort.getAttacking_soldiers()+1);
				}else {
					fort.setDefending_soldiers(fort.getDefending_soldiers()+1);
				}
				sender = new Client_Sender(this.serverport, this.myport, this.fort, this.seq, world_id);
				sender.setMessage_number(2);
				sender.start();	
			} catch (InterruptedException | IOException e) {
				System.out.println("Shutting down build soldiers.");
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
