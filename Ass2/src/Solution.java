import java.util.ArrayList;
import java.util.Random;


public class Solution {

	private Random random;
	private boolean[] sol;
	static private int type;		// 1 = Ugraph, 2 = Ggraph
	private int cutsize;
	
	
	
	public Solution(int graph_type) {
		
		type = graph_type;
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
		
	public Solution(boolean[] solution, int graph_type){
		type = graph_type;
		random = new Random();
		sol = solution;
		cutsize = cutsize(sol);
	}
	
	public Solution(boolean[] solution, int cut, int graph_type){
		type = graph_type;
		random = new Random();
		sol = solution;
		cutsize = cut;
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
	
	public int[] shuffle(int[] solution){
		Random rand = new Random();
		int index1,index2, temp;
        for (int i=solution.length*2; i>0; i--){
            index1 = rand.nextInt(solution.length);
            index2 = rand.nextInt(solution.length);
            
            temp = solution[index1];
            solution[index1] = solution[index2];
            solution[index2] = temp;
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
	
	/**
	 * Swap computes the first improvement for the Swap Vertex Neighbourhood.
	 * It returns the best possible swap
	 * @return whether there was a succesful swap
	 */
	public boolean swap() {
		// get list of partitions
		int[] fst = new int[sol.length/2]; //list of nodes in 0
		int[] snd = new int[sol.length/2]; //list of nodes in 1
		int fstIndex=0,sndIndex=0;
		for(int i=0;i<sol.length;i++){
			if (sol[i]){
				snd[sndIndex] = i;
				sndIndex++;
			} else {
				fst[fstIndex] = i;
				fstIndex++;
			}
		}
		
		//shuffle arrays
		fst = shuffle(fst); 
		snd = shuffle(snd);
		
		// go through them until an improvement is found;
		int gain = 0;
		
		outerLoop:
		for (int it1=0;it1<fst.length;it1++){
			//now fst[it1] ,
			gain = 0;
			sol[fst[it1]] = !sol[fst[it1]];
			gain+= getGain(fst[it1]);
			
			for (int it2=0;it2<snd.length;it2++){
				//now fst[it1] is in 2,
				// snd[it2] is in on
				sol[snd[it2]] = !sol[snd[it2]];
				gain += getGain(snd[it2]);
								
				if (gain>0){
					break outerLoop;
				} else {
					sol[snd[it2]] = !sol[snd[it2]];
				}
			} // end for loop it2
			sol[fst[it1]]=!sol[fst[it1]];
		} // end for loop it1
		
		cutsize -= gain;
		return (gain >0);
	}

	/** 
	 * Invert inverts the partition of the solution
	 * Every 1 of the solution will now  be a 0, and vice versa
	 */
	public void invert() {
		boolean[] invert = new boolean[sol.length];
		
		for(int i=0; i<sol.length; i++) {
			invert[i] = !sol[i];
		}
		sol = invert;	
	}
	
	/**
	 * Calculates the gain after a vertex has been changed.
	 * It assumes that getGain is called after the vertex has been altered!
	 * @param swapper
	 * @return the gain won by changing the partition of the vertex
	 */
	private static int getGain(boolean[] solution, int swapper){
		int gain =0;
		ArrayList<Integer> neighbours = Node.getNeighbours(swapper);
		for (int i =0;i<neighbours.size();i++){
			if(solution[swapper]==solution[i]){ // check if neighbour was in different partition;
				gain++; // if so, now they are in the same (one less cut to make!)
			}else {
				gain--; // if they were in the same, now a cut has to be made
			}
		}
		return gain;
	}
	
	/**
	 * Calculates the gain after a vertex has been changed.
	 * It assumes that getGain is called after the vertex has been altered!
	 * @param swapper
	 * @return the gain won by changing the partition of the vertex
	 */
	private int getGain(int swapper){
		return getGain(sol,swapper);
	}
	
	/**
	 * Perturbation disturbs the current solution, by swapping two vertices,
	 * which are in different partitions. This is performed once!
	 */
	public void perturbation(int numberOfPerturbs) {
		
		if (numberOfPerturbs>1){
			int rand1, rand2; 
			
			// find random node
			rand1 = random.nextInt(sol.length);
			sol[rand1] = !sol[rand1];
			int gain = getGain(rand1);
			
			while( ( (rand2 = random.nextInt (sol.length)) == rand1)
					&& (sol[rand2] == sol[rand1]) ){/* repeat until unique number is found */}
			
			sol[rand2]=!sol[rand2];
			gain+= getGain(rand2);
			cutsize -= gain;
			
			perturbation(numberOfPerturbs-1);
		}
	}
	
	/**
	 * cutsize calculates the number of cuts that are present in the in the current solution
	 * So basically the fitness of the solution
	 * 
	 * @param sol - the solution to be checked, this is a boolean array of size 500.
	 * @return  it returns the total amount of cuts that is needed in the graph in order to obtain
	 * the current solution.
	 */
	public static int cutsize(boolean[] sol) {
		// get list of partitions
		int[] fst = new int[sol.length/2]; //list of nodes in 0
		int fstIndex=0;
		for(int i=0;i<sol.length;i++){
			if (!sol[i]){
				fst[fstIndex] = i;
				fstIndex++;
			}
		}
		
		int cutsize = 0;
		for(int i=0; i < fst.length; i++) {
			float[] coord = {0,0}; // dit moeten de coördinaten worden: (i.getCoordinates();
			ArrayList<Integer> neighbours = Node.getNeighbours(i);
			for (int j =0;j<neighbours.size();i++){
				if(sol[i]!=sol[j]){ // check if neighbour is in different partition;
					if(type==1) {
						cutsize++;
					}
					else {
						float x1 = coord[0];
						float y1 = coord[1];
						float[] coord2 = {0,0}; // opnieuw error: j.getCoordinates();
						float x2 = coord2[0];
						float y2 = coord2[1];
						           
						cutsize += (Math.round(Math.sqrt(Math.abs(x1-x2)*Math.abs(x1-x2) + Math.abs(y1-y2)*Math.abs(y1-y2))));
					}
				}
			}
		}
	
		return cutsize;
	}
	
	
}
