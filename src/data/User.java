package data;

/**
* Object class to create user objects
* @author walder daniel - 015153159
* 
*/
public class User {

	public String ip;
	public int port;
	public int id;
	public boolean alive = true;
	public boolean active = true;
	public boolean settings = false;
	
	public User() {
		
	}
	
	public User (String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public User (String ip) {
		this.ip = ip;
	}
	
	public User (int port) {
		this.port = port;
	}
	
	public User (String ip, int port, int id) {
		this.ip = ip;
		this.port = port;
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isSettings() {
		return settings;
	}

	public void setSettings(boolean settings) {
		this.settings = settings;
	}
	
	
}
