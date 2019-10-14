package client;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import data.Fortress;
import data.Protocol;
import data.Sequence_Number;

/**
 * Sender class. Sends all outgoing messages to the server
 * @author walder - 015153159
 *
 */
public class Client_Sender extends Thread {

	private DatagramSocket sender;
	private InetAddress address;
	private Protocol protocol;
	private Fortress fort;
	private int message_number = 0;
	private int amount;
	
	
	public Client_Sender (int serverport, int myport, Sequence_Number seq, int id) throws IOException {
		this.sender = new DatagramSocket();
		this.address = InetAddress.getByName("localhost");
		this.protocol = new Protocol(this.sender, address, serverport, myport, seq, id);
	}
	
	public Client_Sender (int serverport, int myport, Sequence_Number seq) throws IOException {
		this.sender = new DatagramSocket();
		this.address = InetAddress.getByName("localhost");
		this.protocol = new Protocol(this.sender, address, serverport, myport, seq);
	}
	
	public Client_Sender (int serverport, int myport, Fortress fort, Sequence_Number seq, int id) throws IOException {
		this.sender = new DatagramSocket();
		this.address = InetAddress.getByName("localhost");
		this.protocol = new Protocol(this.sender, address, serverport, myport, seq, id);
		this.fort = fort;
	}
	
	public Client_Sender (int serverport, int myport, Fortress fort, Sequence_Number seq, int amount, int id) throws IOException {
		this.sender = new DatagramSocket();
		this.address = InetAddress.getByName("localhost");
		this.protocol = new Protocol(this.sender, address, serverport, myport, seq, id);
		this.amount = amount;
		this.fort = fort;
	}
	
	/**
	 * Depending on the number other classes set, the sender sends a specific message to the server
	 */
	public void run() {
		
		if(message_number == 0) {
			protocol.register();
		}else if(message_number == 1) {
			protocol.attack_fortress(fort.getX_position(), fort.getY_position(), fort.getUser().getPort(), amount);
		}else if(message_number == 2) {
			protocol.update_values(fort);
		}else if(message_number == 3) {
			protocol.alive_message();
		}else if(message_number == 4) {
			protocol.set_special_settings();
		}
	
	
		sender.close();
		
	}

	public int getMessage_number() {
		return message_number;
	}

	public void setMessage_number(int message_number) {
		this.message_number = message_number;
	}
}
