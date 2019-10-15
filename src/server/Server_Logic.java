package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import data.Parameters;
import data.Fortress;
import data.Listener;
import data.Message_Queue;
import data.Place_Fortress;
import data.Sequence_Number;
import data.Tuple;
import data.User;
import data.World;


/**
* Main logic of the server. Checks all incoming messages and handles them
* @author walder daniel - 015153159
* 
*/
public class Server_Logic extends Thread {


	private Listener listener;
	private Place_Fortress place = new Place_Fortress();
	private Server_Data data;
	private Server_Logic logic;
	private static final int myport = Parameters.server_port;
	private Message_Queue<DatagramPacket> q, world_queue;
	private Message_Queue<Special_Settings> settings_queue;
	private Check_Timeout timeout;
	private Sequence_Number seq;
	private DatagramSocket listener_socket;
	private boolean logic_flag = true;
	private boolean world_flag = true;
	private Special_Settings settings;
	private ExecutorService main_pool, world_pool;
	private List<ExecutorService> pools = new ArrayList<ExecutorService>();
	private List<World> worlds = new ArrayList<World>();
	private World world;
	private int id;
	
	public Server_Logic() {};
	
	public Server_Logic(Message_Queue<DatagramPacket> q, Server_Data data, Sequence_Number seq, Listener listener, Check_Timeout timeout, ExecutorService pool, int id){
		this.world_queue = q;
		this.data = data;
		this.seq = seq;
		this.listener = listener;
		this.timeout = timeout;
		this.world_pool = pool;
		this.id = id;
	}

	
	public static void main (String [] args) {
		Server_Logic server = new Server_Logic();
		server.start_server();
		
	}
	/**
	 * Start server logic including all surrounding classes
	 */
	public void start_server() {
		try {
			main_pool = Executors.newCachedThreadPool();
			q = new Message_Queue<DatagramPacket>();
			listener_socket = new DatagramSocket(myport);
			listener = new Listener(q, listener_socket);
			main_pool.execute(listener);
			create_world(0);
			handle_worlds();
		} catch (IOException | InterruptedException e) {
			stop_server();
		} 
	}


	
	/**
	 * Create a new map 
	 * @param n
	 */
	public void create_world(int n) {
		data = new Server_Data();
		data.init_map();
		seq = new Sequence_Number(0);
		timeout = new Check_Timeout(myport, data, seq, n);
		world_queue = new Message_Queue<DatagramPacket>();
		world_pool = Executors.newCachedThreadPool();
		pools.add(world_pool);
		world = new World(data, seq, timeout, world_queue, n);
		worlds.add(world);
		logic = new Server_Logic(world_queue, data, seq, listener, timeout, world_pool, world.getId());
		world_pool.execute(logic);
		System.out.println("Create new world "+n+"!");

	}
	
	
	/**
	 * Handle all incoming messages and hand it over to the right world
	 * @throws InterruptedException
	 */
	public void handle_worlds() throws InterruptedException {
		Set<String> messages = new HashSet<String>();
		while(logic_flag) {
			if(messages.size() > Parameters.max_message_queue_size) {
				messages.clear();
			}
			DatagramPacket packet;
			while((packet = q.messages.poll()) != null) {
				if(!messages.contains(new String(packet.getData(), 0, packet.getLength()))) {
					messages.add(new String(packet.getData(), 0, packet.getLength()));
					String message = new String(packet.getData(), 0, packet.getLength());
					World tmp_world = worlds.get(worlds.size()-1);
					int world_id;
					if(tmp_world.getData().players == Parameters.max_players && message.charAt(0) == '0') {
						create_world(worlds.size());	
					}
					if(message.charAt(0) == '0') {
						world_id = worlds.size()-1;
					}else {
						char tmp = message.charAt(1);
						StringBuilder sb = new StringBuilder();
						sb.append(tmp);
						world_id = Integer.valueOf(sb.toString());
					}
					Message_Queue<DatagramPacket> tmp_queue = worlds.get(world_id).getWorld_queue();
					tmp_queue.messages.put(packet);
				}
			}
		}
	}
	
	/**
	 * Every world handles their incoming messages
	 */
	public void run() {
		settings_queue = new Message_Queue<Special_Settings>();
		Set<String> messages = new HashSet<String>();
		while(world_flag) {
			if(messages.size() > Parameters.max_message_queue_size) {
				messages.clear();
			}
			DatagramPacket packet;
			while((packet = world_queue.messages.poll()) != null) {
				if(!messages.contains(new String(packet.getData(), 0, packet.getLength()))) {
					messages.add(new String(packet.getData(), 0, packet.getLength()));
					analyse_message(packet);
				}
			}
			if(timeout.close_server.get()) {
				stop_world();
			}
		}
	}
	
	/**
	 * stop server and all threads
	 */
	public void stop_server() {
		logic_flag = false;
		listener.stop_listener();
		main_pool.shutdownNow();
		for(int i = 0; i < pools.size(); i++) {
			pools.get(i).shutdownNow();
		}
	}
	
	/**
	 * Stop world and all threads
	 */
	public void stop_world() {
		world_flag = false;
		world_pool.shutdownNow();
		System.out.println("Shutting down world "+id+".");
	}
	
	
	/**
	 * check if a user exists
	 * @param port
	 * @return
	 */
	public boolean user_exists(String port) {
		for(int i = 0; i < data.getUsers().size(); i++) {
			if(data.getUsers().get(i).getPort() == Integer.valueOf(port)) {
				return true;
			}	
		}
		return false;
	}
	
	/**
	 * register a user, create a fort for him and inform all user
	 * @param port
	 * @param message
	 */
	public void register_user(String port, DatagramPacket message) {
		if(data.players == 0) {
			timeout.setSettings_queue(settings_queue);
			world_pool.execute(timeout);
		}
		inform_player(port, 1);
		User new_user = new User(message.getAddress().toString(), Integer.valueOf(port));
		String[][] update_map = data.getMap();			
		Tuple coordinate = place.find_spot(update_map, data.map_size, data.map_size);
		if(coordinate != null) {
			Fortress fort = new Fortress(coordinate.x_toString(), coordinate.y_toString(), new_user);
			data.fortresses.add(fort);
			data.players++;
			update_map[coordinate.getX()][coordinate.getY()] = port;
			data.setMap(update_map);
			data.users.add(new_user);
			inform_players_of_new_player(fort);
			inform_new_player_of_players(new_user);
		}
	}
	
	/**
	 * inform a single client 
	 * @param clientport
	 * @param number message number
	 */
	public void inform_player(String clientport, int number) {
		Server_Sender sender;
		try {
			sender = new Server_Sender(Integer.valueOf(clientport), myport, seq, id);
			sender.setMessage_number(number);
			world_pool.execute(sender);
		} catch (NumberFormatException | IOException e) {
			System.out.println("Shutting down server logic.");
		}
		
	}
	
	/**
	 * handle a fortress attack
	 * @param attx attacked fort x coordinate
	 * @param atty attacked fort y coordinate
	 * @param defenderport 
	 * @param attackerport
	 * @param soldiers attacking soldiers
	 */
	public void attack_fortress(int attx, int atty, int defenderport, int attackerport, int soldiers) {

		Fortress fort;
		Server_Sender sender;
		
		synchronized(data.getFortresses()) {
			try {
				for(int i = 0; i < data.getFortresses().size(); i++) {
					if(data.getFortresses().get(i).getX_positionAsInt() == attx && data.getFortresses().get(i).getY_positionAsInt() == atty && data.getFortresses().get(i).getUser().getPort() == defenderport) {
						if(((data.getFortresses().get(i).getDefending_soldiers()*0.75) + (data.getFortresses().get(i).getAttacking_soldiers() * 0.25)) >= soldiers) {
							fort = data.getFortresses().get(i);
							fort.setDefending_soldiers(fort.getDefending_soldiers()-(int)(soldiers*0.75));
							fort.setAttacking_soldiers(fort.getAttacking_soldiers() - (int)(soldiers*0.25));
							if(fort.getDefending_soldiers() < 0) {fort.setDefending_soldiers(0);}
							if(fort.getAttacking_soldiers() < 0) {fort.setAttacking_soldiers(0);}
							
							sender = new Server_Sender(Integer.valueOf(defenderport), fort, myport, seq, defenderport, id);
							sender.setMessage_number(4);
							world_pool.execute(sender);
							sender = new Server_Sender(Integer.valueOf(attackerport), fort, myport, seq, defenderport, id);
							sender.setMessage_number(4);
							world_pool.execute(sender);
							break;
						}else {
							fort = data.getFortresses().get(i);
							fort.setUser(data.findUser(attackerport));
							fort.setAttacking_soldiers((int)(soldiers*0.60));
							data.getFortresses().get(i).setDefending_soldiers(0);
							data.getMap()[attx][atty] = String.valueOf(attackerport);
							fort = data.getFortresses().get(i);
							sender = new Server_Sender(Integer.valueOf(attackerport), fort, myport, seq, attackerport, id);
							sender.setMessage_number(4);
							world_pool.execute(sender);
							sender = new Server_Sender(Integer.valueOf(defenderport), fort, myport, seq, attackerport, id);
							sender.setMessage_number(4);
							world_pool.execute(sender);
							inform_players_of_new_player(fort);
							break;
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * analyse a single message
	 * @param message
	 */
	public void analyse_message(DatagramPacket message) {
		String string_message = new String(message.getData(), 0, message.getLength());
		if(string_message.charAt(0) == '0'){
			String port = string_message.substring(6, 10);
			if(data.getPlayers() > data.max_amount_players) {
				inform_player(port, 2);
			}
			else if(!user_exists(port)) {
				register_user(port, message);
			}else {
				inform_player(port, 2);
			}
		}
		else if(string_message.charAt(0) == '1'){
			String attackerport = string_message.substring(6, 10);
			String x = string_message.substring(10, 12);
			String y = string_message.substring(12, 14);
			String defenderport = string_message.substring(14,18);
			String soldiers = string_message.substring(18,22);
			attack_fortress(Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(defenderport), Integer.valueOf(attackerport), Integer.valueOf(soldiers));
		}
		else if(string_message.charAt(0) == '2'){
			String port = string_message.substring(6, 10);
			String x = string_message.substring(10, 12);
			String y = string_message.substring(12, 14);
			String level = string_message.substring(14,18);
			String attacking = string_message.substring(18,22);
			String defending = string_message.substring(22,26);
			data.udpate_fortress(port, x, y, level, attacking, defending);	
		}
		else if(string_message.charAt(0) == '3'){
			String port = string_message.substring(6, 10);
			update_timeout(Integer.valueOf(port));
			inform_player(port, 3);	
		}
		else if(string_message.charAt(0) == '7'){
			String port = string_message.substring(6, 10);
			User user = data.findUser(Integer.valueOf(port));
			user.setSettings(true);
			settings = new Special_Settings(Integer.valueOf(port), myport, seq, id);
			try {
				settings_queue.messages.put(settings);
			} catch (InterruptedException e) {
				System.out.println("Shutting down server logic,");
			}
			world_pool.execute(settings);
		}
	}
	
	/**
	 * Update timeout of a client
	 * @param port
	 */
	public void update_timeout(int port) {
		synchronized(data.getUsers()) {
			for(int i = 0; i < data.getUsers().size(); i++) {
				if(data.getUsers().get(i).getPort() == port) {
					data.getUsers().get(i).setAlive(true);
				}
			}
		}
	}
	
	/**
	 * inform all client of a new client
	 * @param fortress
	 */
	public void inform_players_of_new_player(Fortress fortress) {
		List<User> list = new ArrayList<User>();
		synchronized(data) {
			list.addAll(data.users);
		}
		for(int x = 0; x < list.size(); x++) {
			try {
				Server_Sender sender = new Server_Sender(list.get(x).getPort(), fortress, myport, seq, id);
				world_pool.execute(sender);
			} catch (IOException e) {
				System.out.println("Shutting down logic.");
			}
		}
	}
	/**
	 * inform new client of all other clients
	 * @param user
	 */
	public void inform_new_player_of_players(User user) {
		List<Fortress> forts = new ArrayList<Fortress>();
		synchronized(data) {
			forts.addAll(data.fortresses);
		}
		for(int x = 0; x < forts.size(); x++) {
			try {
				if(forts.get(x).getUser().getPort() != user.getPort()) {
					Server_Sender sender = new Server_Sender(user.getPort(), forts.get(x), myport, seq, id);
					world_pool.execute(sender);
				}
			} catch (IOException e) {
				System.out.println("Shutting down logic.");
			}
		}
	}
	
	
	
	
}
