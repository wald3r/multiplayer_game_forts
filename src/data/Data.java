package data;

import java.util.ArrayList;
import java.util.List;

import data.Parameters;

public class Data {

	public String[][] map;
	public List<Fortress> fortresses = new ArrayList<Fortress>();
	public int map_size = Parameters.map_size;
	public int max_amount_players = Parameters.max_players;
	public List<User> users = new ArrayList<User>();
	public int players = 0;
	
	public void init_map() {
		
		map = new String[map_size][map_size];
		for(int i = 0; i < map.length; i++) {
			for(int x = 0; x < map.length; x++) {
				map[i][x] = "0000";
			}
		}
	}


	public String[][] getMap() {
		return map;
	}


	public void setMap(String[][] map) {
		this.map = map;
	}


	public List<Fortress> getFortresses() {
		return fortresses;
	}


	public void setFortresses(List<Fortress> fortresses) {
		this.fortresses = fortresses;
	}


	public int getMap_size() {
		return map_size;
	}


	public void setMap_size(int map_size) {
		this.map_size = map_size;
	}


	public List<User> getUsers() {
		return users;
	}


	public void setUsers(List<User> users) {
		this.users = users;
	}


	public int getPlayers() {
		return players;
	}


	public void setPlayers(int players) {
		this.players = players;
	}


	public int getMax_amount_players() {
		return max_amount_players;
	}
}
