package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import data.Parameters;
import data.Fortress;
import data.Message_Queue;
import data.Sequence_Number;
import data.User;
import data.Listener;
/**
 * Main class of the client. Handles all incoming messages and controls all the other classes! 
 * 
 * Attention! This class is almost similar to the Client_Logic class. This class simulates opponents and contains a simple logic to participate in the game. 
 * 
 * @author walder daniel - 015153159
 * 
 */
public class Rebel_Logic extends Thread {

	private Listener listener;
	private Registration registration;
	private Client_Sender sender;
	private Client_Data data;
	private int myport;
	private final int serverport = 5000;
	private Message_Queue<DatagramPacket> q;
	private Mine_Resources mine;
	private Improve_Mining improve_mining;
	private Build_Soldiers build_attack_soldiers;
	private boolean flag;
	private Sequence_Number seq;
	private Alive_Message alive;
	public boolean logic;
	private DatagramSocket socket;
	private ExecutorService pool;
	private static boolean settings;
	private int id;

	
	public Rebel_Logic(int myport) {
		this.myport = myport;
	}
	
	public Rebel_Logic(Listener listener, Client_Data data, boolean logic, Message_Queue<DatagramPacket> q, int myport, boolean flag, Sequence_Number seq, Alive_Message alive, Registration reg, DatagramSocket socket, ExecutorService pool) {
		this.listener = listener;
		this.data = data;
		this.q = q;
		this.myport = myport;
		this.flag = flag;
		this.seq = seq;
		this.alive = alive;
		this.registration = reg;
		this.socket = socket;
		this.pool = pool;
		this.logic = logic;
	}
	
	
	public static void main (String [] args) {
		
		int port;
		int number_of_clients;
		
		if(args.length == 2) {
			port = Integer.valueOf(args[0].toString());
			number_of_clients = Integer.valueOf(args[1].toString());
		}else {
			System.out.println("Not enough arguments! ");
			return;
		}
		
		
		settings = false;
		for(int i = 0; i < number_of_clients; i++) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				System.out.println("Shutting down rebel logic.");
			}
			settings = !settings;
			new Rebel_Logic(port++).start_client();
		}
		
	}
	
	public void stop_client() {
		logic = false;
		listener.stop_listener();
		alive.send_alive_messages.set(false);
		pool.shutdownNow();
	}

	
	public void still_running() {
		if(!alive.keep_running.get()) {
			System.out.println("Connection loss!");
			stop_client();
		}
	}
	
	public void start_client() {
		
		data = new Client_Data();
		data.init_map();
		q = new Message_Queue<DatagramPacket>();
		seq = new Sequence_Number(0);
		
		try {
			pool = Executors.newCachedThreadPool();
			registration = new Registration(serverport, myport, seq, data);
			socket = new DatagramSocket(myport);
			listener = new Listener(q, socket);
			alive = new Alive_Message(serverport, myport, seq);
			sender = new Client_Sender(serverport, myport, seq);
			Rebel_Logic logic1 = new Rebel_Logic(listener, data, true, q, myport, false, seq, alive, registration, socket, pool);
			Rebel_Logic logic2 = new Rebel_Logic(listener, data, true, q, myport, true, seq, alive, registration, socket, pool);
			registration.start();
			pool.execute(logic1);
			pool.execute(listener);
			registration.join();
			if(!data.registered.get()) {
				stop_client();
			}else {
				pool.execute(logic2);
				if(settings) {
					sender.setMessage_number(4);
					pool.execute(sender);
				}
			}
		} catch (IOException | InterruptedException e) {
			System.out.println("Shutting down rebel logic.");
		}
		
	}
	
	
	/**
	 * Contains a simple logic to participate in the game. After some has passed the client produces soldiers. 
	 */
	public void run() {

		if(this.flag) {
			while(logic) {
				try {
					Thread.sleep(Parameters.rebel_logic_waiting_time);
				} catch (InterruptedException e) {
					System.out.println("Shutting down rebel logic.");
				}
				if(data.myFortresses.size() > 0) {
					Fortress fort = data.myFortresses.get(0);
					if(fort.getResources() > 5 && fort.getDefending_soldiers() < 200 && fort.getMining_level() == 1) {
						produce_soldiers(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2));
					}else if(fort.getResources() > 5 && fort.getDefending_soldiers() < 300 && fort.getMining_level() == 2) {
						produce_soldiers(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2));
					}else if(fort.getResources() > 5 && fort.getDefending_soldiers() < 400 && fort.getMining_level() == 3) {
						produce_soldiers(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2));
					}else if(fort.getResources() > 5 && fort.getDefending_soldiers() < 500 && fort.getMining_level() == 4) {
						produce_soldiers(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2));
					}else if(fort.getResources() > 5 && fort.getDefending_soldiers() < 600 && fort.getMining_level() == 5) {
						produce_soldiers(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2));
					}else if(fort.getResources() > 5 && fort.getDefending_soldiers() < 700 && fort.getMining_level() == 6) {
						produce_soldiers(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2));
					}else if (fort.getResources() > (20*fort.getMining_level())) {
						improve_mining(Integer.valueOf(0));
					}
				}
				still_running();
			}
			
		}else {
			Set<String> messages = new HashSet<String>();
			while(logic) {
				DatagramPacket packet;
				while((packet = q.messages.poll()) != null) {
					if(!messages.contains(new String(packet.getData(), 0, packet.getLength()))) {
						messages.add(new String(packet.getData(), 0, packet.getLength()));
						analyse_message(packet);
					}
				}
				still_running();

			}
		}
		
	}
	
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
				boolean added_myforts;
				Fortress fort_exist;
				synchronized(data) {
					added_myforts = data.already_added_myforts(x_pos, y_pos);
					fort_exist = data.getFort(x_pos, y_pos);
				
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
			}
			else if(string_message.charAt(0) == '4'){
				if(string_message.contains("ACCEPTED")) {
					char tmp = string_message.charAt(1);
					StringBuilder sb = new StringBuilder();
					sb.append(tmp);
					id = Integer.valueOf(sb.toString());
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
				String x = string_message.substring(10, 12);
				String y = string_message.substring(12, 14);
				String level = string_message.substring(14,18);
				String attacking = string_message.substring(18,22);
				String defending = string_message.substring(22,26);
				String winner = string_message.substring(26,30);
				after_attack(Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(attacking), Integer.valueOf(defending), Integer.valueOf(winner), Integer.valueOf(level));
			}else if(string_message.charAt(0) == '7'){
				
			}
		}catch(StringIndexOutOfBoundsException e) {
			
		}
	}
	
	
	public void after_attack(int x, int y, int attacking, int defending, int winner, int level) {
		for(int i = 0; i < data.getFortresses().size(); i++) {
			if(data.getFortresses().get(i).getX_positionAsInt() == x && data.getFortresses().get(i).getY_positionAsInt() == y) {
				synchronized(data) {
					Fortress fort = data.getFortresses().get(i);
					if(fort.getUser().getPort() == myport && winner == myport) {
						fort.setAttacking_soldiers(attacking);
						fort.setDefending_soldiers(defending);
					}else if(fort.getUser().getPort() == myport && winner != myport) {
						fort.getUser().setPort(winner);
						data.getMyFortresses().remove(fort);
						data.setMyFortressesCounter(data.getMyFortressesCounter()-1);
						data.map[x][y] = String.valueOf(winner);
						if(!any_myfortresses_left()) {
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
						if(!other_fortresses_left()) {
							stop_client();
						}
					}
				}
			}	
		}
	}
	
	public boolean any_myfortresses_left() {
		if(data.getMyFortressesCounter() == 0) {
			return false;
		}
		return true;
	}
	
	public boolean other_fortresses_left() {
		if(data.getFortresses().size() != data.getMyFortressesCounter()) {
			return true;
		}
		return false;
	}
	
	public void start_mining(Fortress fort) {
		mine = new Mine_Resources(fort, alive);
		pool.execute(mine);
	}
	
	public void improve_mining(int n) {
		Fortress fort = data.myFortresses.get(n);
		improve_mining = new Improve_Mining(fort, serverport, myport, this.seq);
		pool.execute(improve_mining);
	}
	
	public void produce_soldiers(int n, int k, int l) {
		Fortress fort = data.myFortresses.get(n);
		build_attack_soldiers = new Build_Soldiers(fort, serverport, myport, k, l, this.seq);
		pool.execute(build_attack_soldiers);
	}
	
}
