import java.util.ArrayList;

import java.util.Random;

/**
 * Class Solution is the owner of all the properties of a Solution to 
 * either a U or a G graph. It contains the binary solution string of length 500,
 * the fitness of that solution, measured by the amount of cuts that are needed in order
 * to have 2 partitions. Also it contains some methods to modify the solution.
 * @author Myrna, Julius
 *
 */
public class Solution {

	private Random random;
	private boolean[] sol;
	static private int type;		// 1 = Ugraph, 2 = Ggraph
	private long cutsize;
	
	/**
	 * Basic constructor. It instantiates a random solution and
	 * calculates its cutsize. This is based on the graph type.
	 * 
	 * @param graph_type an integer 0,1,2 indicating what kind of graph is 
	 * been used. 0 means Ugraph with coordinate distance, 1 U graph, and 2 G graph.
	 */
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
		cutsize = -1;
		while (cutsize <0){
			sol = shuffle(sol);
			cutsize = cutsize(sol);
		}
	}
		
	/**
	 * Second constructor, in case a binary solution string is already
	 * available. 
	 * @param solution binary solution to the graph
	 * @param graph_type the type of graph, 0, 1 or 2. See the basic constructor 
	 * for more explaination
	 */
	public Solution(boolean[] solution, int graph_type){
		type = graph_type;
		random = new Random();
		sol = solution;
		cutsize = cutsize(sol);
		
		while (cutsize <0){
			System.out.println("The solution was to sucky bakki!");
			sol = shuffle(sol);
			cutsize = cutsize(sol);
		}
	}
	
	/**
	 * Third constructor in case of a readily available solution.
	 * @param solution the binary solution array
	 * @param cut the cutsize of that solution
	 * @param graph_type an integer 0,1 or 2 indicating the kind of graph
	 */
	public Solution(boolean[] solution, long cut, int graph_type){
		type = graph_type;
		random = new Random();
		sol = solution;
		cutsize = cut;
		//System.out.println("Correct cutsize? "+(cutsize(sol)==cut));
	}
	
	/**
	 * Shuffle permutes the solution. For every entry, the value
	 * is swapped with that of another (random) entry
	 * @param solution the binary array to shuffle
	 * @return the shuffled array
	 */
	public boolean[] shuffle(boolean[] solution) {
		
		for(int i=0; i < solution.length; i++){
			int rand = random.nextInt(solution.length);
			boolean temp = solution[i];
	        solution[i] = solution[rand];
	        solution[rand] = temp;
		}
		return solution;
	}
	
	/**
	 * Generic shuffle function for an array with integers.
	 * @param array the array of integer to shuffle
	 * @return the shuffled array
	 */
	public int[] shuffle(int[] array){
		Random rand = new Random();
		int index1,index2, temp;
        for (int i=array.length; i>0; i--){
            index1 = rand.nextInt(array.length);
            index2 = rand.nextInt(array.length);
            
            temp = array[index1];
            array[index1] = array[index2];
            array[index2] = temp;
        }
        return array;
	}

	
	public void setSol(boolean[] soll) {
		sol = soll;
	}
	public boolean[] getSol() {
		return sol;
	}
	public void setCutsize(long maxValue) {
		cutsize = maxValue;
	}
	public long getCutsize() {
		return cutsize;
	}
	
	/**
	 * localSearch is the function that performs a SVN localsearch on 
	 * the instance of the solution. If swaps to entries in the solution
	 * and if succesfull it repeats itself. Otherwise it stops and returns.
	 * @return the number of successful swaps performed
	 */
	public int localSearch() {
		//System.out.print("Starting localsearch . . ");
		// swap bits and stop local search when no improvement found
		int nrOfSwaps =0;
		
		//System.out.println(cutsize + " vs " + cutsize(sol));
		while( swap() ){
			//keep swapping until no improvement is found anymore
			nrOfSwaps++;
		}
		
		//System.out.println("Swaps made: "+nrOfSwaps);
		//System.out.println("Current fitness: "+cutsize + "  " + cutsize(sol));
		return nrOfSwaps;
	}
	
	/**
	 * Swap computes the first improvement for the Swap Vertex Neighbourhood.
	 * It returns the best possible swap
	 * @return whether there was a successful swap
	 */
	public boolean swap() {
		// get list of partitions
		int[] fst = new int[sol.length/2]; //list of nodes in 0
		int[] snd = new int[sol.length/2]; //list of nodes in 1
		int fstIndex=0,sndIndex=0;
		for(int i=1;i<=sol.length;i++){
			if (sol[i-1]){
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
		long gain=0, gain2=0;
		outerLoop:
		for (int it1=0;it1<fst.length;it1++){
			//now fst[it1] ,
			//boolean[] old = sol.clone();
			sol[fst[it1]-1] = !sol[fst[it1]-1];
			gain = getGain(fst[it1]); // give Id of node (ID is 1-based (whereas sol is 0-based))
			//System.out.println("1) " + (cutsize-gain) + " vs "+ cutsize(sol) + " Mutated?" + !sol.equals(old)+ " "+gain);
			for (int it2=0;it2<snd.length;it2++){
				//now fst[it1] is in 2,
				// snd[it2] is in on
				//old = sol.clone();
				sol[snd[it2]-1] = !sol[snd[it2]-1];
				gain2 = gain + getGain(snd[it2]);
				//System.out.println("\t 2) "+ (cutsize-gain2) + " vs "+ cutsize(sol) + " Mutated?" + !sol.equals(old) + " "+gain2);				
				if (gain2>0){
					break outerLoop;
				} else {
					sol[snd[it2]-1] = !sol[snd[it2]-1];
					gain2=0;
				}
			} // end for loop it2
			sol[fst[it1]-1]=!sol[fst[it1]-1];
		} // end for loop it1
		
		cutsize -= gain2;
		return (gain2 > 0);
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
	 * @param idSwapper
	 * @return the gain won by changing the partition of the vertex
	 */
	private static long getGain(boolean[] solution, int idSwapper){
		long gain =0;
		ArrayList<Integer> neighbours = Node.getNeighbours(idSwapper);
		//quick check
		//System.out.println("Getting neighbours of : " +  idSwapper + " vs " + Node.getNode(idSwapper).getId() );
		for (int i =0;i<neighbours.size();i++){
			if(solution[idSwapper-1]==solution[neighbours.get(i)-1]){ // check if neighbour was in different partition;
				if (type==0){
					gain += Node.distance(idSwapper,neighbours.get(i));
				} else {
					gain++; // if so, now they are in the same (one less cut to make!)
				}
			}else {
				if (type==0){
					gain -= Node.distance(idSwapper,neighbours.get(i));
				}else {
					gain--; // if they were in the same, now a cut has to be made
				}
			}
		}
		return gain;
	}
	
	/**
	 * Calculates the gain after a vertex has been changed.
	 * It assumes that getGain is called after the vertex has been altered!
	 * @param idSswapper
	 * @return the gain won by changing the partition of the vertex
	 */
	private long getGain(int idSwapper){
		return getGain(sol,idSwapper);
	}
	
	/**
	 * Perturbation disturbs the current solution, by swapping two vertices,
	 * which are in different partitions. This is performed once!
	 */
	public void perturbation(int numberOfPerturbs) {
		//System.out.println("Perturbing (size "+numberOfPerturbs+")");
		while (numberOfPerturbs>1){
			int rand1, rand2; 
			
			//System.out.println("Starting: "+cutsize(sol));
			// find random node
			rand1 = random.nextInt(sol.length);
			sol[rand1] = !sol[rand1];
			long gain = getGain(rand1+1);
			//System.out.println("Gain: "+gain+" New cutsize: "+(cutsize-(gain))+" Cmp: "+cutsize(sol));
			long gain2;
			//System.out.println("Gain1: "+gain+" New cutsize: "+(cutsize-gain)+" Cmp: "+cutsize(sol));
			
			while( ( (rand2 = random.nextInt (sol.length)) == rand1)
					|| (sol[rand2] != sol[rand1]) ){/* repeat until unique number is found */}
			
			sol[rand2]=!sol[rand2];
			gain2 = getGain(rand2+1);
			//System.out.println("Gain2: "+gain+" New cutsize: "+(cutsize-gain)+" Cmp: "+cutsize(sol));
			while (cutsize-(gain+gain2) <0){
				sol[rand2]=!sol[rand2];
				while( ( (rand2 = random.nextInt (sol.length)) == rand1)
						|| (sol[rand2] != sol[rand1]) ){/* repeat until unique number is found */}
				
				sol[rand2]=!sol[rand2];
				gain2 = getGain(rand2+1);
				
			}
			//System.out.println("gain2: "+gain2);
			//System.out.println("Gain2: "+gain2+" New cutsize: "+(cutsize-(gain+gain2))+" Cmp: "+cutsize(sol));
			cutsize -= (gain+gain2);
			
			numberOfPerturbs--;
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
	public static long cutsize(boolean[] sol) {
		// get list of the 0 partition (the 1 partition would yield the same end result!)
		int[] fst = new int[sol.length/2+1]; //list of nodes in 0
		int fstIndex=0;
				
		for(int i=1;i<=sol.length;i++){
			if (!sol[i-1]){
				fst[fstIndex] = i;
				fstIndex++;
			}
		}
		
		//In case a bit has been flipped (only one), account for the zero entries at the end; 
		int cap=fst.length;
		while(fstIndex<fst.length){
			fstIndex++; cap--;
		}

		/*System.out.println("size of fst "+fst.length);
		System.out.print("Cutsize fst<");
		for (int k=0; k<fst.length;k++){
			System.out.print(fst[k] + ", ");
		}
		System.out.println(">");*/

		long cutsize = 0;
		ArrayList<Integer> neighbours;
		for(int i=0; i < cap; i++) {
			neighbours = Node.getNeighbours(fst[i]);
			for (int j =0;j<neighbours.size();j++){
				if(sol[fst[i]-1]!=sol[neighbours.get(j)-1]){ // check if neighbour is in different partition;
					if(type==1 ||type==2) {
						cutsize++;
					} else { // type = 0
						cutsize += Node.distance(fst[i],neighbours.get(j));
					}
				}
			} //end neighbour loop
		} // end 0-partition loop

		return cutsize;
	}
}
