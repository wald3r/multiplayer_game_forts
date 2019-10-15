 package data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import data.Parameters;
/**
* Protocol class to communicate between server and client. 
* @author walder daniel - 015153159
* 
*/
public class Protocol {

	private DatagramSocket sender;
	private DatagramSocket listener;
	private byte[] buf = new byte[Parameters.message_size];
	private InetAddress address;
	private int serverport;
	private int clientport;
	private Sequence_Number seq;
	private int id;


	public Protocol(DatagramSocket listener) {
		this.listener = listener;
	}
	
	public Protocol (DatagramSocket sender, InetAddress address, int serverport, int clientport, Sequence_Number seq, int id) {
		this.sender = sender;
		this.address = address;
		this.serverport = serverport;
		this.clientport = clientport;
		this.seq = seq;
		this.id = id;
	}
	
	public Protocol (DatagramSocket sender, InetAddress address, int serverport, int clientport, Sequence_Number seq) {
		this.sender = sender;
		this.address = address;
		this.serverport = serverport;
		this.clientport = clientport;
		this.seq = seq;
	}
	
	/**
	 * Increase thread safe the sequence number of the client / server
	 * @return
	 */
	public int get_seq() {
		int i;
		synchronized(seq) {
			seq.setS(seq.getS()+1);
			i = seq.getS();
		}
		return i;
	}
	
	/**
	 * send message tripled(fec method). Plus there is an implemented chance that the message gets lost. 
	 * @param message
	 */
	public void send_message_tripled(String message) {

		reset_buffer();
		buf = message.getBytes();
		try {
			for(int i = 0; i < Parameters.fec_redundant_method; i++) {
				Random rand = new Random();
				int likely = rand.nextInt(100);
				likely += 1;
				if(likely > Parameters.message_loss) {
					if(serverport != Parameters.server_port) {
						System.out.println("MESSAGE to "+this.serverport+": "+message);
					}
					this.sender.send(new DatagramPacket(buf, buf.length, this.address, this.serverport));
				}
			}
		} catch (IOException e) {
			System.out.println("Shutting down protocol.");
		}
	}
	
	/**
	 * Generate a String to fill a message up to the end. 
	 * @return
	 */
	public String gen_special_settings() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 502; i++) {
			sb.append("X");
		}
		return sb.toString();
	}
	
	
	/**
	 * Inform clients of a fortress. Solely public information. 
	 * @param x coordinate
	 * @param y coordinate
	 * @param port owner
	 */
	public void update_fortress(String x, String y, int port) {
		String str = "8"+id+padding(get_seq())+Integer.toString(this.clientport)+x+y+Integer.toString(port)+"\0";
		send_message_tripled(str);
	}
	
	/**
	 * Client informs server that he wants special settings
	 */
	public void set_special_settings() {
		String str = "7"+id+padding(get_seq())+this.clientport+"EXTRASETTINGS";
		send_message_tripled(str);
	}
	
	/**
	 * Server sends client special settings
	 */
	public void send_special_settings() {
		String message = gen_special_settings();
		String str = "7"+id+padding(get_seq())+this.clientport+message;
		send_message_tripled(str);
	}
	
	/**
	 * Client registers
	 */
	public void register(){
		String str = "0X"+padding(get_seq())+this.clientport+"NEWUSER";
		send_message_tripled(str);
	}
	
	/**
	 * client attacks a fort
	 * @param x coordinate of the attacking fort
	 * @param y coordinate of the attacking fort
	 * @param attackport attacker
	 * @param number amount of soldiers
	 */
	public void attack_fortress(String x, String y, int attackport, int number ){
		String str = "1"+id+padding(get_seq())+this.clientport+x+y+attackport+padding(number);
		send_message_tripled(str);
	}
	
	/**
	 * padd the value with 0's
	 * @param value
	 * @return
	 */
	public String padding(int value) {
		if(value < 10) {
			return "000"+String.valueOf(value);
		}else if(value > 9 && value < 100) {
			return "00"+String.valueOf(value);
		}else if(value > 99 && value < 1000) {
			return "0"+String.valueOf(value);
		}else if(value > 999 && value < 10000) {
			return String.valueOf(value);
		}
		return "";
	}
	
	/**
	 * Client informs server about his fortress
	 * @param fort
	 */
	public void update_values(Fortress fort) {
		String str = "2"+id+padding(get_seq())+this.clientport+fort.getX_position()+fort.getY_position()+padding(fort.getMining_level())+padding(fort.getAttacking_soldiers())+padding(fort.getDefending_soldiers());
		send_message_tripled(str);
	}
	
	/**
	 * Server informs clients after an attack who the winner is
	 */
	public void inform_opponents(Fortress fort, int port) {
		String str = "6"+id+padding(get_seq())+this.clientport+fort.getX_position()+fort.getY_position()+padding(fort.getMining_level())+padding(fort.getAttacking_soldiers())+padding(fort.getDefending_soldiers())+port;
		send_message_tripled(str);
	}
	
	/**
	 * Alive message from the client
	 */
	public void alive_message() {
		
		String str = "3"+id+padding(get_seq())+this.clientport+"ALIVE";
		send_message_tripled(str);
	}
	
	/**
	 * Server informs client that registration worked
	 */
	public void registered() {	
		String str = "4"+id+padding(get_seq())+this.clientport+"ACCEPTED";
		send_message_tripled(str);
	}
	/**
	 * Server informs client that registration didn't work
	 */
	public void not_registered() {
		String str = "4"+id+padding(get_seq())+this.clientport+"FAILED";
		send_message_tripled(str);
	}
	/**
	 * ACK message
	 */
	public void ack() {
		String str = "5"+id+padding(get_seq())+this.clientport+"ACK";
		send_message_tripled(str);
	}
	/**
	 * Receiving a message
	 * @return
	 */
	public DatagramPacket receive_message() {
		
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		reset_buffer();
		try {
			listener.receive(packet);
		} catch (IOException e) {
		}
		if(listener.getLocalPort() == Parameters.server_port) {
			System.out.println("MESSAGE received: "+new String(packet.getData(), 0, packet.getLength()));
		}
		return packet;
	}
	/**
	 * Reset the buffer
	 */
	public void reset_buffer() {
		buf = new byte[Parameters.message_size];
	}
}
