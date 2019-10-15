package data;

import data.Parameters;


/**
* Object class for fortresses. 
* @author walder daniel - 015153159
* 
*/
public class Fortress {


	public User user;
	public String x_position;
	public String y_position;
	private int resources;
	private int mining_level;
	private int defending_soldiers;
	private int attacking_soldiers;
	
	public Fortress(String x, String y, User user) {
		this.x_position = x;
		this.y_position = y;
		this.user = user;
		this.resources = Parameters.start_resources;
		this.mining_level = Parameters.start_mining_level;
		this.defending_soldiers = Parameters.start_defending_soldiers;
		this.attacking_soldiers = Parameters.start_attacking_soldiers;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getX_position() {
		return x_position;
	}

	public void setX_position(String x_position) {
		this.x_position = x_position;
	}

	public String getY_position() {
		return y_position;
	}

	public int getY_positionAsInt() {
		return Integer.valueOf(y_position);
	}
	
	public int getX_positionAsInt() {
		return Integer.valueOf(x_position);
	}

	public void setY_position(String y_position) {
		this.y_position = y_position;
	}

	public int getResources() {
		return resources;
	}

	public void setResources(int resources) {
		if(this.resources == 9999) {
			
		}else {
			this.resources = resources;
		}
	}

	public int getMining_level() {
		return mining_level;
	}

	public void setMining_level(int mining_level) {
		this.mining_level = mining_level;
	}

	public int getDefending_soldiers() {
		return defending_soldiers;
	}

	public void setDefending_soldiers(int defending_soldiers) {
		if(this.defending_soldiers == 9999) {
			
		}else {
			this.defending_soldiers = defending_soldiers;
		}
	}

	public int getAttacking_soldiers() {
		return attacking_soldiers;
	}

	public void setAttacking_soldiers(int attacking_soldiers) {
		if(this.attacking_soldiers == 9999) {
			
		}else {
			this.attacking_soldiers = attacking_soldiers;
		}
	}
	
	public String toString() {
		return ("Position: "+this.getX_position()+"/"+this.getY_position()+"  Level:"+this.getMining_level()+"  Attacking: "+this.getAttacking_soldiers()+"  Defending: "+this.getDefending_soldiers());
	}
	
	
}
