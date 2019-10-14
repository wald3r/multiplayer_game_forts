package server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import data.Fortress;
import data.Protocol;
import data.Sequence_Number;


/**
* Sender class to send messages to clients
* @author walder daniel - 015153159
* 
*/
public class Server_Sender extends Thread {

	private final DatagramSocket sender;
	private InetAddress address;
	private Protocol protocol;
	private int message_number = 0;
	private Fortress fort;
	private int old_port;
	
	
	public Server_Sender (int serverport, int clientport, Sequence_Number seq, int id) throws IOException {
		this.sender = new DatagramSocket();
		this.address = InetAddress.getByName("localhost");
		this.protocol = new Protocol(this.sender, address, serverport, clientport, seq, id);
	}
	
	public Server_Sender (int serverport, Fortress fort, int clientport, Sequence_Number seq, int id) throws IOException {
		this.sender = new DatagramSocket();
		this.address = InetAddress.getByName("localhost");
		this.protocol = new Protocol(this.sender, address, serverport, clientport, seq, id);
		this.fort = fort;
	}
	
	public Server_Sender (int serverport, Fortress fort, int clientport, Sequence_Number seq, int old_port, int id) throws IOException {
		this.sender = new DatagramSocket();
		this.address = InetAddress.getByName("localhost");
		this.protocol = new Protocol(this.sender, address, serverport, clientport, seq, id);
		this.fort = fort;
		this.old_port = old_port;
	}
	
	/**
	 * Depending on the message_number the server has set, a different message gets send to a client
	 */
	public void run() {		
		if(message_number == 0) {
			protocol.update_fortress(fort.x_position, fort.y_position, fort.getUser().getPort());	
		}else if(message_number == 1) {
			protocol.registered();
		}else if(message_number == 2) {
			protocol.not_registered();
		}else if(message_number == 3) {
			protocol.ack();
		}else if(message_number == 4) {
			protocol.inform_opponents(fort, old_port);
		}else if(message_number == 5) {
			protocol.send_special_settings();
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
