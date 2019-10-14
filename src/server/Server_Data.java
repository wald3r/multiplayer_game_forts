package server;

import data.Data;
import data.User;


/**
* Data class of the server. Stores all important data.
* @author walder daniel - 015153159
* 
*/
public class Server_Data extends Data {


	public Server_Data() {}

	
	/**
	 * update a single fortress
	 * @param port user
	 * @param x coordinate
	 * @param y coordinate
	 * @param level mining
	 * @param attacking soldiers
	 * @param defending soldiers
	 */
	public void udpate_fortress(String port, String x, String y, String level, String attacking, String defending) {
		for(int i = 0; i < fortresses.size(); i++) {
			if(x.equals(fortresses.get(i).getX_position()) && y.equals(fortresses.get(i).getY_position()) && fortresses.get(i).getUser().getPort() == Integer.valueOf(port)) {
				fortresses.get(i).setMining_level(Integer.valueOf(level));
				fortresses.get(i).setAttacking_soldiers(Integer.valueOf(attacking));
				fortresses.get(i).setDefending_soldiers(Integer.valueOf(defending));
			}
		}
	}
	
	
	/**
	 * find a user
	 * @param port
	 * @return
	 */
	public User findUser(int port) {
		
		for(int i = 0; i < users.size(); i++) {
			if(users.get(i).getPort() == port) {
				return users.get(i);
			}
		}
		return null;
	}
	
}
