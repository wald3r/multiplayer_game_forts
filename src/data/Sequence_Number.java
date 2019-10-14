package data;

/**
* Sequence class to attach the number to a message and increase it thread safe
* @author walder daniel - 015153159
* 
*/
public class Sequence_Number {

	private int s;
	
	public Sequence_Number(int s) {
		this.s = s;
	}

	public int getS() {
		if(s + 1 > 9999) {
			return 0;
		}
		return s;
	}

	public void setS(int s) {
		this.s = s;
	}
	
}
