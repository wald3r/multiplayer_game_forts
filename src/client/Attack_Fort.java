package client;

import java.io.IOException;

import data.Fortress;
import data.Sequence_Number;

/**
 * Informs server of an attack after some time has passed. 
 * @author walder - 015153159
 *
 */
public class Attack_Fort extends Thread{

	private Fortress attackfort;
	private Fortress myfort;
	private int serverport;
	private int myport;
	private Client_Sender sender;
	private int amount;
	private Sequence_Number seq;
	private int time;
	private int world_id;
	
	public Attack_Fort(int serverport, int myport, Fortress attackfort, Fortress myfort, int amount, Sequence_Number seq, int time) {
		this.attackfort = attackfort;
		this.myfort = myfort;
		this.myport = myport;
		this.serverport = serverport;
		this.seq = seq;
		this.amount = amount;
		this.time = time;
	}
	
	public void run() {
	
		try {
			myfort.setAttacking_soldiers(myfort.getAttacking_soldiers()-amount);
			Thread.sleep(time*10000);
			sender = new Client_Sender(serverport, myport, attackfort, seq, amount, world_id);
			sender.setMessage_number(1);
			sender.start();
		} catch (InterruptedException | IOException e) {
			System.out.println("Shutting down attack_fort.");
		}
		
	}

	public int getWorld_id() {
		return world_id;
	}

	public void setWorld_id(int world_id) {
		this.world_id = world_id;
	}

	
}
