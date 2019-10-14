package client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import data.Data;
import data.Fortress;

public class Client_Data extends Data {

	public List<Fortress> myFortresses = new ArrayList<Fortress>();
	private int myFortressesCounter = 0;
	public AtomicBoolean registered = new AtomicBoolean(false);;

	public Client_Data() {
		
	}

	/**
	 * print general map
	 */
	public void print_map() {
		for(int i = 0; i < map.length; i++) {
			System.out.print("-----");
			System.out.print(i);

		}
		System.out.println("");
		for(int i = 0; i < map.length; i++) {
			System.out.print(i);
			for(int x = 0; x < map.length; x++) {
				System.out.print("--");
				System.out.print(map[i][x]);
			}
			System.out.println("");

		}
		System.out.println("");
	}
	
	
	/**
	 * print all my fortresses
	 */
	public void print_fortresses() {
		System.out.println("All Fortresses:");
		for(int i = 0; i < myFortresses.size(); i++) {
			System.out.println("Fortress Number: "+i+"  Position:" +myFortresses.get(i).x_position+"/"+myFortresses.get(i).y_position+"  Resources: "+myFortresses.get(i).getResources()+"  Mining lvl.: "+myFortresses.get(i).getMining_level()+"  Def. Soldiers: "+myFortresses.get(i).getDefending_soldiers()+"  Att. Soldiers: "+myFortresses.get(i).getAttacking_soldiers());
		}
		System.out.println("");
	}
	
	/**
	 * Check if any of the clients fortresses left
	 * @return true || false
	 */
	public boolean any_myfortresses_left() {
		if(getMyFortressesCounter() == 0) {
			System.out.println("You lost the game!");
			return false;
		}
		return true;
	}
	
	/**
	 * Check if any adversaries left
	 * @return true || false
	 */
	public boolean other_fortresses_left() {
		if(getFortresses().size() != getMyFortressesCounter()) {
			return true;
		}
		System.out.println("You won the game!");
		return false;
	}
	
	/**
	 * Check if fort is already mine
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean already_added_myforts(String x, String y) {
		for(int i = 0; i < myFortresses.size(); i++) {
			if(myFortresses.get(i).getX_position().equals(x) && myFortresses.get(i).getY_position().equals(y)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Look for a fort
	 * @param x coordinate of the fort
	 * @param y coordinate of the fort
	 * @return
	 */
	public Fortress getFort(String x, String y) {
		for(int i = 0; i < fortresses.size(); i++) {
			if(fortresses.get(i).getX_position().equals(x) && fortresses.get(i).getY_position().equals(y)) {
				return fortresses.get(i);
			}
		}
		return null;
	}
	
	
	public List<Fortress> getMyFortresses() {
		return myFortresses;
	}

	public void setMyFortresses(List<Fortress> myFortresses) {
		this.myFortresses = myFortresses;
	}
	
	public void addToMyFortresses (Fortress fort) {
		this.myFortresses.add(fort);
		this.myFortressesCounter++;
		
	}
	
	public int getMyFortressesCounter() {
		return myFortressesCounter;
	}

	public void setMyFortressesCounter(int myFortressesCounter) {
		this.myFortressesCounter = myFortressesCounter;
	}

	public AtomicBoolean getRegistered() {
		return registered;
	}

	public void setRegistered(AtomicBoolean registered) {
		this.registered = registered;
	}
	
}
