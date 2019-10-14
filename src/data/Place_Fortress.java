package data;



/**
* Class to find an empty spot for a new fortress on the map. The idea is to start in the middle and to go via spiral form from the inside to the outside. 
* @author walder daniel - 015153159
* 
*/
public class Place_Fortress {
	
	public Tuple find_spot(String[][] map, int m, int n) {
		
			Tuple tlist[] = new Tuple[m*n]; 
			Tuple rev_tlist[] = new Tuple[m*n];
			String[] b = new String[m*n];
			String[] rev_b = new String[m*n];
			
			int i = 0, j = 0; 
			int z = 0; 
		
			while (i < m && j < n) { 
	        
		
				for (int p = i; p < n; p++) { 
					tlist[z] = new Tuple (i, p);
					b[z] = map[i][p];
					z++;

				} 
				i++;
				
				for (int p = i+1; p < m; p++) { 
					tlist[z] = new Tuple(p,n-1);
					b[z] = map[p][n-1];
					z++;
				} 
				n--;
			
				if (m-1 != i) { 
					for (int p = n-2; p >= j; p--) { 
						tlist[z] = new Tuple(m-1, p);
						b[z] = map[m-1][p];
						z++;
					}
					m--;
				} 
			
				if ((n - 1) != j)  { 
		            for (int p = m - 2; p > i; p--)  { 
						tlist[z] = new Tuple(p, j);
						b[z] = map[p][j];
						z++;
					} 
		            j++;
				} 
			}
			int o = 0;
			for (int x = z-1; x>=0 ; x--) { 
				rev_tlist[o] = tlist[x];
				rev_b[o] = b[x]; 
				o++;
			} 
			
			
			for(o = 0; i < rev_b.length; o++) {
					if(!already_occupied(rev_b[o])) {
						return rev_tlist[o];
					}
			}
			return null;
		}
	
		public boolean already_occupied(String str) {
			if(str.equals("0000")){
				return false;
			}
			return true;
		}
		


}
