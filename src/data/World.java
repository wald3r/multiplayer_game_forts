package data;

import server.Check_Timeout;
import server.Server_Data;


public class World {

	private Server_Data data;
	private Sequence_Number seq;
	private Check_Timeout timeout;
	private Message_Queue world_queue;
	private int id;
	
	public World(Server_Data data, Sequence_Number seq, Check_Timeout timeout, Message_Queue queue, int id) {
		this.data = data;
		this.seq = seq;
		this.timeout = timeout;
		this.world_queue = queue;
		this.id = id;
	}
	
	public Data getData() {
		return data;
	}
	public void setData(Server_Data data) {
		this.data = data;
	}
	public Sequence_Number getSeq() {
		return seq;
	}
	public void setSeq(Sequence_Number seq) {
		this.seq = seq;
	}
	public Check_Timeout getTimeout() {
		return timeout;
	}
	public void setTimeout(Check_Timeout timeout) {
		this.timeout = timeout;
	}
	public Message_Queue getWorld_queue() {
		return world_queue;
	}
	public void setWorld_queue(Message_Queue world_queue) {
		this.world_queue = world_queue;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
