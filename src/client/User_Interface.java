package client;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import data.Fortress;
import data.Parameters;
import data.Sequence_Number;


/**
* User interface class of the client. Displays the map and interacts with the client
* @author walder daniel - 015153159
* 
*/
public class User_Interface extends Thread {

	private Client_Data data;
	private Improve_Mining improve_mining;
	private Build_Soldiers build_soldiers;
	private Attack_Fort attack;
	private Sequence_Number seq;
	private Scanner sc = new Scanner(System.in);
	public AtomicBoolean keep_running = new AtomicBoolean(true);
	public AtomicBoolean waiting = new AtomicBoolean(true);
	private int serverport;
	private int myport;
	private ExecutorService pool;
	private int world_id;

	
	public User_Interface(int serverport, int myport, Client_Data data, Sequence_Number seq, ExecutorService pool) {
		this.data = data;
		this.serverport = serverport;
		this.myport = myport;
		this.seq = seq;
		this.pool = pool;
	}
	
	public void close_scanner() {
		sc.close();
	}

	/**
	 * Simple interface for the client
	 */
	public void run() {
		while(keep_running.get()) {
			data.print_map();
			data.print_fortresses();
			System.out.println("Soldier production costs/time: "+Parameters.soldier_production_costs+"/"+Parameters.soldier_production_time+"   Improve mining costs/time: "+Parameters.improve_mining_costs+"*Mining level/"+Parameters.improve_mining_time);
			System.out.println("");
			System.out.println("1. Attack");
			System.out.println("2. Improve Mining");
			System.out.println("3. Produce Attack Soldiers");
			System.out.println("4. Produce Defence Soldiers");
			System.out.println("5. Update View!");
			System.out.println("Make your move:");
			sc = new Scanner(System.in); 
			try {
				waiting.set(true);
				int number = sc.nextInt();
				waiting.set(false);
				if(number == 1) {
					interface_attack_fort();
				}else if(number == 2) {
					interface_improve_mining();
				}else if(number == 3) {
					interface_soldiers(1);
				}else if(number == 4) {
					interface_soldiers(2);
				}else if(number == 5) {
					
				}else {
					System.out.println("Wrong input!");
				}
			} catch (InputMismatchException e) {
				System.out.println("Wrong input!");
			} catch (NoSuchElementException e) {
			}
		}
	}
	
	
	
	/**
	 * Calculate the distance to the fort and inform Attack_Fort class. 
	 * @param attackfort fort to attack
	 * @param myfort fort of the client
	 * @param amount amount of soldiers
	 */
	public void logic_attack_fort(Fortress attackfort, Fortress myfort, int amount) {
		
		int diffx = myfort.getX_positionAsInt() - attackfort.getX_positionAsInt();
		int diffy= myfort.getY_positionAsInt() - attackfort.getY_positionAsInt();
		if(diffx < 0) {
			diffx *= -1;
		}
		if(diffy < 0) {
			diffy *= -1;
		}
		System.out.println("It will take you "+diffx+diffy+" sec to get there!");
		attack = new Attack_Fort(serverport, myport, attackfort, myfort, amount, seq, diffx+diffy);
		attack.setWorld_id(world_id);
		pool.execute(attack);
		
		
	}
	
	/**
	 * interface to attack a fort
	 */
	public void interface_attack_fort() {
		System.out.println("From which fort? Type in Fortress Number:");
		int forts = data.getMyFortressesCounter();
		sc = new Scanner(System.in);
		int n = sc.nextInt();
		if(n < forts && n > -1) {
			
			System.out.println("Which fort you wish to attack? Type in position like this: 00/00");
			sc = new Scanner(System.in);
			String position = sc.nextLine();
			if(position.length() == 5) {
				String x = position.substring(0,2);
				String y = position.substring(3,5);
				if(!data.isMyFortress(x,  y)) {
					Fortress attackfort = data.getFort(x, y);
					if(attackfort != null) {
						System.out.println("With how many Soldiers?");
						sc = new Scanner(System.in);
						int amount;
						while(true) {
							amount = sc.nextInt();
							if(data.getFortresses().get(n).getAttacking_soldiers() >= amount) {
								break;
							}
						}
						logic_attack_fort(attackfort, data.getFortresses().get(n), amount);
						
					}else {
						System.out.println("There is no fort!");
					}
				}
				else {
					System.out.println("This is your own fort!");
				}
			}else {
				System.out.println("Wrong input!");
			}
		}else {
			System.out.println("Wrong input!");
		}
	}
	
	/**
	 * interface to produce soldiers
	 * @param type
	 */
	public void interface_soldiers(int type) {
		System.out.println("Which fort?");
		int forts = data.getMyFortressesCounter();
		sc = new Scanner(System.in);
		int n = sc.nextInt();
		System.out.println(forts);
		if(n < forts && n > -1) {
			int amount;
			while(true) {
				System.out.println("How many?");
				sc = new Scanner(System.in);
				amount = sc.nextInt();
				if(amount > -1) {
					break;
				}
			}
			if(data.myFortresses.get(n).getResources() > Parameters.soldier_production_costs*amount) {
				produce_soldiers(n, amount, type);
			}else {
				System.out.println("Not enough resources!");
			}
		}else {
			System.out.println("Wrong input!");
		}
		
	}
	
	/**
	 * interface to improve mining
	 */
	public void interface_improve_mining() {
		System.out.println("Which Fortress?");
		int forts = data.getMyFortressesCounter();
		sc = new Scanner(System.in); 
		int n = sc.nextInt();
		System.out.println(n+" "+forts);
		if(n < forts && n > -1) {
			logic_improve_mining(n);
		}else {
			System.out.println("Wrong input!");
		}
	}
	
	/**
	 * Inform improve mining class to improve the mining
	 * @param n
	 */
	public void logic_improve_mining(int n) {
		Fortress fort = data.myFortresses.get(n);
		improve_mining = new Improve_Mining(fort, serverport, myport, this.seq);
		improve_mining.setWorld_id(this.getWorld_id());
		pool.execute(improve_mining);
	}
	
	/**
	 *  inform build soldiers class to produce soldiers
	 */
	public void produce_soldiers(int n, int amount, int type) {
		Fortress fort = data.myFortresses.get(n);
		build_soldiers = new Build_Soldiers(fort, serverport, myport, amount, type, this.seq);
		build_soldiers.setWorld_id(this.getWorld_id());
		pool.execute(build_soldiers);
	}

	public int getWorld_id() {
		return world_id;
	}

	public void setWorld_id(int world_id) {
		this.world_id = world_id;
	}
}
