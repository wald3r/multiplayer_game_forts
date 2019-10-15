package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import data.Parameters;
import data.Fortress;
import data.Message_Queue;
import data.Sequence_Number;
import data.User;

/**
* Protocol class to check if a client has disconnected 
* @author walder daniel - 015153159
* 
*/
public class Check_Timeout extends Thread {

	
	private Server_Data data;
	private int myport;
	private Sequence_Number seq;
	public AtomicBoolean close_server = new AtomicBoolean(false);
	private Message_Queue<Special_Settings> settings_queue;
	private List<Special_Settings> settings_list = new ArrayList<Special_Settings>();
	private int id;
	
	public Check_Timeout(int myport, Server_Data data, Sequence_Number seq, int id) {
		this.data = data;
		this.myport = myport;
		this.seq = seq;
		this.id = id;
	}
	
	/**
	 * if a client has disconnected they will get removed
	 * @param user
	 */
	public void change_fortresses(User user) {
		
		for(int i = 0; i < data.getFortresses().size(); i++) {
			if(data.getFortresses().get(i).getUser().getPort() == user.getPort()) {
				User rebel_user = new User(9999);
				data.getFortresses().get(i).setUser(rebel_user);
				data.getMap()[data.getFortresses().get(i).getX_positionAsInt()][data.getFortresses().get(i).getY_positionAsInt()] = "9999";
				inform_players(data.getFortresses().get(i));
			}
		}
	}
	
	/**
	 * Check if a client has sent his alive messages otherwise remove him. 
	 */
	public void run() {
		
		try {
			while(true) {
				Thread.sleep(Parameters.check_timeout);
				List<User> deleteduser = new ArrayList<User>();
				synchronized(data.getUsers()) {
					for(int i = 0; i < data.getUsers().size(); i++) {
						if(data.getUsers().get(i).isAlive()) {
							data.getUsers().get(i).setAlive(false);
						}else {
							User to_delete = data.getUsers().get(i);
							deleteduser.add(to_delete);
							data.players--;
							stop_sending_special_settings(to_delete);
							System.out.println("Player "+data.getUsers().get(i).getPort()+" disconnected!");
						}
					}
					data.getUsers().removeAll(deleteduser);
				}
				for(int i = 0; i < deleteduser.size(); i++) {
					synchronized(data) {
						change_fortresses(deleteduser.get(i));
					}
				}
				if(data.getPlayers() == 0) {
					close_server.set(true);
				}
			}
			
		} catch (InterruptedException e) {
			System.out.println("Shutting down timeout.");
		}
	}
	
	
	public void stop_sending_special_settings(User to_delete) throws InterruptedException {
		Special_Settings set;
		while((set = settings_queue.messages.poll()) != null) {
			if(set.getServerport() == to_delete.getPort()){
				set.keep_running.set(false);
			}else {
				settings_list.add(set);
			}	
		}
		settings_queue.messages.addAll(settings_list);
	}
	
	/**
	 * inform players of changes
	 * @param fortress
	 */
	public void inform_players(Fortress fortress) {
		for(int x = 0; x < data.getUsers().size(); x++) {
			try {
				Server_Sender sender = new Server_Sender(data.getUsers().get(x).getPort(), fortress, myport, seq, id);
				sender.start();
			} catch (IOException e) {
				System.out.println("Shutting down timeout.");
			}
		}
	}

	public Message_Queue<Special_Settings> getSettings_queue() {
		return settings_queue;
	}

	public void setSettings_queue(Message_Queue<Special_Settings> settings_queue) {
		this.settings_queue = settings_queue;
	}
	
}
