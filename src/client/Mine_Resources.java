package client;

import data.Fortress;
import data.Parameters;

/**
* Mine resources class. Mine resources and update the fortress
* @author walder - 015153159
*
*/
public class Mine_Resources extends Thread {

	Fortress fort;
	public Alive_Message alive;
	
	public Mine_Resources(Fortress fort, Alive_Message alive) {
		this.fort = fort;
		this.alive = alive;
	}
	
	/**
	 * After some time has passed the resources get increased by 1
	 */
	public void run() {
		
		while(alive.keep_running.get()) {
			try {
				Thread.sleep(Parameters.mine_resources_time);
				fort.setResources(fort.getResources()+1*fort.getMining_level());
			} catch (InterruptedException e) {
				System.out.println("Shutting down mine resources.");
			}
		}
	}
}
