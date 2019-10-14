package data;


/**
* A helper class to place the fortresses
* @author walder daniel - 015153159
* 
*/
public class Tuple {

	private final int x;
	private final int y;
	
	public Tuple(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public String x_toString() {
		if(this.x > 9) {
			return String.valueOf(this.x);
		}
		return "0"+String.valueOf(this.x);
	}
	
	public String y_toString() {
		if(this.y > 9) {
			return String.valueOf(this.y);
		}
		return "0"+String.valueOf(this.y);
	}
}
