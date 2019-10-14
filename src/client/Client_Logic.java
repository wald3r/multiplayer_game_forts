package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import data.Fortress;
import data.Message_Queue;
import data.Sequence_Number;
import data.User;
import data.Listener;

/**
 * Main class of the client. Handles all incoming messages and controls all the other classes!
 * @author walder daniel - 015153159
 * 
 */
public class Client_Logic extends Thread {

	private Listener listener;
	private Client_Data data;
	private Client_Sender sender;
	private User_Interface user_interface;
	private Registration registration;
	private int myport = 4000-1;
	private final int serverport = 5000;
	private Message_Queue q;
	private Mine_Resources mine;
	private Alive_Message alive;
	private Sequence_Number seq;
	private static boolean logicFlag = true;
	private DatagramSocket socket;
	private ExecutorService pool;
	private static boolean settings;
	private int id;
	
	
	
	public Client_Logic() {
	}
	
	public Client_Logic(int myport, Message_Queue q, Listener listener, Client_Data data, Sequence_Number seq, Alive_Message alive, Registration reg, DatagramSocket socket, User_Interface user_interface, ExecutorService pool) {
		this.listener = listener;
		this.data = data;
		this.myport = myport;
		this.q = q;
		this.seq = seq;
		this.alive = alive;
		this.registration = reg;
		this.socket = socket;
		this.user_interface = user_interface;
		this.pool = pool;
	}
	
	/**
	 * Start client
	 * @param args
	 */
	public static void main (String [] args) {
		
		if (args.length == 2) {
			settings = true;
		}else {
			settings = false;
		}
		Client_Logic client = new Client_Logic();
		client.start_client();
	}
	
	/**
	 * Check if there is a timeout!
	 */
	public void still_running() {
		if(!alive.keep_running.get()) {
			System.out.println("Connection loss!");
			stop_client();
		}
	}
	
	/**
	 * Stop everything
	 */
	public void stop_client() {
		logicFlag = false;
		listener.stop_listener();
		alive.send_alive_messages.set(false);
		user_interface.keep_running.set(false);
		user_interface.close_scanner();
		pool.shutdownNow();
	}
	
	/**
	 * Start the client with all classes and threads
	 */
	public void start_client() {
		
		data = new Client_Data();
		data.init_map();
		q = new Message_Queue();
		seq = new Sequence_Number(0);
		
		try {
			pool = Executors.newCachedThreadPool();
			registration = new Registration(serverport, myport, seq, data);
			socket = new DatagramSocket(myport);
			listener = new Listener(q, socket);
			alive = new Alive_Message(serverport, myport, seq);
			user_interface = new User_Interface(serverport, myport, data, seq, pool);
			sender = new Client_Sender(serverport, myport, seq);
			Client_Logic logic = new Client_Logic(myport, q, listener, data, seq, alive, registration, socket, user_interface, pool);
			System.out.println("Start registering...");
			registration.start();
			pool.execute(logic);
			pool.execute(listener);
			registration.join();
			if(!data.registered.get()) {
				System.out.println("Registration failed!");
				stop_client();
			}else {
				System.out.println("Starting the game!");
				pool.execute(user_interface);
				if(settings) {
					sender.setMessage_number(4);
					pool.execute(sender);
				}
			}
		} catch (IOException | InterruptedException e) {
			System.out.println("Shutting down client logic.");
		}
	}
	
	/**
	 * Main thread method. Checks for incoming messages from Client_Listener
	 */
	public void run() {

		Set<String> messages = new HashSet<String>();
		while(logicFlag) {
			DatagramPacket packet;
			if(messages.size() > 15) {
				messages.clear();
			}
			while((packet = q.messages.poll()) != null) {
				if(!messages.contains(new String(packet.getData(), 0, packet.getLength()))) {
					messages.add(new String(packet.getData(), 0, packet.getLength()));
					analyse_message(packet);
				}
			}
			still_running();
		}
	}
		

	/**
	 * Analyse every incoming message
	 * @param message
	 */
	public void analyse_message(DatagramPacket message) {
		String string_message = new String(message.getData(), 0, message.getLength());
		try {
			if(string_message.charAt(0) == '8'){
				String port = string_message.substring(14, 18);
				String y_pos = string_message.substring(12, 14);
				String x_pos = string_message.substring(10, 12);
				User new_user = new User(Integer.valueOf(port));
				Fortress fort = new Fortress(x_pos, y_pos, new_user);
				String[][] update_map = data.getMap();
				boolean added_myforts = data.already_added_myforts(x_pos, y_pos);
				Fortress fort_exist = data.getFort(x_pos, y_pos);
				if(Integer.valueOf(port) == myport && !added_myforts) {
					data.addToMyFortresses(fort);
					update_map[Integer.valueOf(x_pos)][Integer.valueOf(y_pos)] = "XXXX";
					start_mining(fort);
				}else if(!added_myforts) {
					update_map[Integer.valueOf(x_pos)][Integer.valueOf(y_pos)] = port;
				}
				if(fort_exist == null) {
					data.fortresses.add(fort);
					data.setMap(update_map);
				}
			}
			else if(string_message.charAt(0) == '4'){
				if(string_message.contains("ACCEPTED")) {
					char tmp = string_message.charAt(1);
					StringBuilder sb = new StringBuilder();
					sb.append(tmp);
					id = Integer.valueOf(sb.toString());
					user_interface.setWorld_id(id);
					alive.setWorld_id(id);
					pool.execute(alive);
					data.registered.set(true);
					registration.received.set(true);
				}
			}
			else if(string_message.charAt(0) == '5'){
				alive.new_message.set(true);
			}
			else if(string_message.charAt(0) == '6'){
				int x = Integer.valueOf(string_message.substring(10, 12));
				int y = Integer.valueOf(string_message.substring(12, 14));
				int level = Integer.valueOf(string_message.substring(14,18));
				int attacking = Integer.valueOf(string_message.substring(18,22));
				int defending = Integer.valueOf(string_message.substring(22,26));
				int winner = Integer.valueOf(string_message.substring(26,30));
				after_attack(x, y, attacking, defending, winner, level);
			}else if(string_message.charAt(0) == '7'){
				
			}
		}catch(StringIndexOutOfBoundsException e) {
			
		}
	}
	
	/**
	 * Handle the message which comes in after an attack
	 * @param x - coordinate of the attacked fort
	 * @param y - coordinate of the attacked fort
	 * @param attacking - number of attacking soldiers left
	 * @param defending - number of defending soldiers left
	 * @param winner - winner port
	 * @param level - mining level of the fort
	 */
	public void after_attack(int x, int y, int attacking, int defending, int winner, int level) {
		for(int i = 0; i < data.getFortresses().size(); i++) {
			if(data.getFortresses().get(i).getX_positionAsInt() == x && data.getFortresses().get(i).getY_positionAsInt() == y) {
				synchronized(data.getFortresses()) {
					Fortress fort = data.getFortresses().get(i);
					if(fort.getUser().getPort() == myport && winner == myport) {
						fort.setAttacking_soldiers(attacking);
						fort.setDefending_soldiers(defending);
						System.out.println("You defended an attack at "+x+"/"+y+"!");
					}else if(fort.getUser().getPort() == myport && winner != myport) {
						fort.getUser().setPort(winner);
						data.getMyFortresses().remove(fort);
						data.setMyFortressesCounter(data.getMyFortressesCounter()-1);
						data.map[x][y] = String.valueOf(winner);
						System.out.println("You lost a fortress at "+x+"/"+y+"!");
						if(!data.any_myfortresses_left()) {
							stop_client();
						}
						
					}else if(fort.getUser().getPort() != myport && winner == myport) {
						fort.getUser().setPort(winner);
						fort.setAttacking_soldiers(attacking);
						fort.setDefending_soldiers(0);
						fort.setMining_level(level);
						data.addToMyFortresses(fort);
						data.map[x][y] = "XXXX";
						start_mining(fort);
						System.out.println("You won the battle at "+x+"/"+y+"!");
						if(!data.other_fortresses_left()) {
							stop_client();
						}
					}else if(fort.getUser().getPort() != myport && winner != myport) {
						System.out.println("You lost the battle at "+x+"/"+y+"!");
					}
				}
			}	
		}
	}
	
	/**
	 * Start mining when client conquers a new fort
	 * @param fort
	 */
	public void start_mining(Fortress fort) {
		mine = new Mine_Resources(fort, alive);
		pool.execute(mine);
	}
	
}
