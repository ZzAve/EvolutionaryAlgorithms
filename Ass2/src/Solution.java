import java.util.Random;


public class Solution {

	private Random random;
	private boolean[] sol;
	private int cutsize;
	
	public Solution() {
		
		random = new Random();
		sol = new boolean[500];
			
		for(int i=0; i<250; i++) {
			sol[i] = false;
		}
		for(int i=250; i<500; i++) {
			sol[i] = true;
		}
			
		sol = shuffle(sol);
	}
		
	public boolean[] shuffle(boolean[] solution) {
		
		
		for(int i=0; i < solution.length; i++){
			int rand = i + random.nextInt (solution.length-i);
			boolean temp = solution[i];
	        solution[i] = solution[rand];
	        solution[rand] = temp;
		}
		return solution;
	}

	public void cutsize(int cut) {
		setCutsize(cut);
	}

	public void setSol(boolean[] soll) {
		sol = soll;
	}

	public boolean[] getSol() {
		return sol;
	}

	public void setCutsize(int cuts) {
		cutsize = cuts;
	}

	public int getCutsize() {
		return cutsize;
	}
}
