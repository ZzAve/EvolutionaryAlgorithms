import java.lang.management.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class GraphBipartioning {
	
	private static Random random;
	private static int numRuns;
	protected static int type;		// 1 = Ugraph, 2 = Ggraph
	
	//static int popsize = 1;
	//static boolean[][] population = new boolean[popsize][500];
	
	public static void main(String[] args) throws IOException {
		
		random = new Random();
		numRuns = 30;
		type = 0;			// 1 = Ugraph, 2 = Ggraph // 0 = Ugraph with distance
		
		// create all nodes
		parse(type);			
		
		Answer[] results = new Answer[numRuns];
		
	
		for(int i=0; i<numRuns; i++) {
			System.out.println("Round "+(i+1)+"/"+numRuns+")");
			// entry zero returns the minimum cutsize, entry one the number of vertex swaps;
			results[i] = multiLS(500);
			//results[i] = iteratedLS(3); // include perturbation size (2,3,4,5,... ?)
			//results[i] = geneticLS(100); // or 100
		}

		
		//write data to file
		System.out.println("Beginnen met schrijven");
		try {
	        BufferedWriter out1 = new BufferedWriter(new FileWriter("./results/results_short.txt"),32678);
	        BufferedWriter out2 = new BufferedWriter(new FileWriter("./results/results_long.txt"),32678);
	        
	        String settingsString1 = "bestid,bestVal,mean,median,variance,avgSwaps,avgTime [ms],type";
	        String settingsString2 = "Run,bestVal,cpuTime[ms],nrOfSwaps,type";
	        String settings1 = "";
	        String settings2 = "";
	        
	        // short file
        	settings1 += Statistics.best(results)[0]+",";
        	settings1 += Statistics.best(results)[1] + ",";
        	settings1 += Statistics.mean(results)+ ",";
        	settings1 += Statistics.median(results)+ ",";
        	settings1 += Statistics.variance(results)+",";
        	settings1 += Statistics.avgSwap(results)+",";
        	settings1 += Statistics.avgTime(results)+",";
        	settings1 += type;
        	out1.write(settingsString1);
        	out1.newLine();
        	out1.write(settings1);
        	out1.newLine();
        	out1.close();
        	
        	out2.write(settingsString2);
        	out2.newLine();
	        for (int i=0; i<results.length;i++){        	             
	        	// long file
	        	settings2="";
	        	settings2 += i + ",";
	        	settings2 += results[i].solution.getCutsize()+",";
	        	settings2 += (results[i].time/1000000.0)+ ",";
	        	settings2 += results[i].nrOfSwaps;
	        	settings2 += type;
	        	out2.write(settings2);
	        	out2.newLine();
	        }
	        out2.close();
	    } catch (IOException e) {
	    	System.err.println("FileNotFoundException: " + e.getMessage());
	    }
		
	    System.out.println("Done!");
	}
	
	/**
	 * The Multi-Start Local Search algorithm
	 * @return
	 */
	public static Answer multiLS(int nrOfStarts) {
		long startCPUtime = getCpuTime();
		Solution best = new Solution(new boolean[500],Long.MAX_VALUE,type);
		int nrOfSwaps =0;
		Solution solution;
		System.out.print("\t Perfroming multiLS ");
		for (int search=1;search<=nrOfStarts;search++){
			if (search%50==0) System.out.print(". ");
			if (search%250==0) System.out.print(search+" ");
			
			// generate solution
			solution = new Solution(type);
			
			//apply local search
			nrOfSwaps += solution.localSearch();
			if(solution.getCutsize() < best.getCutsize()) best = solution;
		}	
		System.out.println();
		System.out.println("\t\t Total number of swaps for "+nrOfStarts+" starts: "+nrOfSwaps);
		
		return new Answer(best,nrOfSwaps,(getCpuTime() - startCPUtime));
	}

	/**
	 * The iterated local search algorithm
	 * @param perturb
	 * @return
	 */
	public static Answer iteratedLS(int perturb) {
		long startCPUtime = getCpuTime();
		System.out.print("\t Perfroming iteratedLS ");	
		int nrOfSwaps = 0; int threshold = 500; int count=threshold;
		Solution solution = new Solution(type);
		Solution oldSolution;
						
		//System.out.println("Current fit: "+solution.getCutsize());
		//Apply local search on new solution
		int unchanged=0;
		nrOfSwaps+= solution.localSearch();
		oldSolution = solution;
		while (unchanged<10){
			if(nrOfSwaps-threshold>count){System.out.print(". ");count+=threshold;}
			
			// Create new solution, which is a perturb solution of the old optimum
			solution = new Solution(oldSolution.getSol(),oldSolution.getCutsize(), type);
			solution.perturbation(perturb);
			
			//Apply local search on new solution
			nrOfSwaps+= solution.localSearch();
			
			if (solution.getCutsize() < oldSolution.getCutsize()){
				unchanged = 0;
				oldSolution = solution;
			} else {
				unchanged++;
			}
		}
		System.out.println();
		System.out.println("\t\t Total number of swaps: "+nrOfSwaps);
		
		return new Answer(oldSolution,nrOfSwaps,(getCpuTime() - startCPUtime));
	}
	
	/**
	 * The genetic local search algorithm
	 * @param popsize
	 * @return
	 */
	public static Answer geneticLS(int popsize) {
		long startCPUtime = getCpuTime();
		System.out.print("\t Performing geneticLS ");
		Solution[] solutions = new Solution[popsize];
		int nrOfSwaps =0; int threshold=5000; int count=threshold;
		
		// generate population and apply local search
		for(int i=0; i < popsize; i++) {
			if(nrOfSwaps-threshold>count){System.out.print(". ");count+=threshold;}
			solutions[i] = new Solution(type);			
			nrOfSwaps += solutions[i].localSearch();
		}
		
		// sort the solutions
		solutions = quickSort(solutions);
		
		int rand1,rand2;
		// recombine and mutate
		boolean changed;
		int unchanged =0;
		while (unchanged<10*popsize){
			if(nrOfSwaps-threshold>count){System.out.print(". ");count+=threshold;}
			//get two parents
			rand1 = random.nextInt(popsize);
			while ((rand2 = random.nextInt(popsize))==rand1){/*repeat until unique number is found*/}
			
			// Create the child, and perform localsearch to find a local optimum
			Solution temp = recombine(solutions[rand1],solutions[rand2]);
			nrOfSwaps += temp.localSearch();
			
			
			// compare to worst and if better, replace
			changed = false; // assume no better solution is found			
			if(temp.getCutsize() < solutions[solutions.length-1].getCutsize()){ // verify if better solution is found
				//ensure that solution is not already present
				int i;
				for (i=0;i<solutions.length;i++){
					if (solutions[i].getSol().equals(temp.getSol())){
						break;
					}
				}
				// if for loop did not break, the new solution is unique
				// And changed is set to true (since i equals solutions.length is at no point the break was hit)
				changed = (i==solutions.length); 
			}
			
			if (changed){
				solutions = insert(solutions,temp);
				unchanged = 0;
			} else {
				unchanged++;
			}
				//System.out.println("change! "+(unchanged==0)+ " " +temp.getCutsize() );
		}		
		System.out.println();
		System.out.println("\t\t Total number of swaps: "+nrOfSwaps + " in "+  ((getCpuTime() - startCPUtime)/1000000) + "ms");
		return new Answer(solutions[0],nrOfSwaps,getCpuTime() - startCPUtime);
	}
		
	
	/**
	 * Parse tries to import the files which hold the information about the graphs.
	 * If it does not work out, it throws an exception.
	 * @param type the type of graph one wants to explore. 1 for U graph, anything else for G graph
	 * @throws IOException in case the file cannot be found and or read.
	 */
	public static void parse(int type) throws IOException {
		FileInputStream stream;
		if(type==1 || type ==0) {
			stream = new FileInputStream("U500.05.txt");
		}
		else {
			stream = new FileInputStream("G500.005.txt");
		}
		
		DataInputStream in = new DataInputStream(stream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
               
        // Read lines and add nodes
        String line;
        while( (line = br.readLine()) != null){
        	new Node(line, type);
        }
	}

	/**
	 * Hammingdistance calculates the hamming distance between two solution strings
	 * The Hamming distance is defined as the number of entries that need to be changed in
	 * the first solution, in order to get the second solution. 
	 * @param sol1 the first solution
	 * @param sol2 the second solution
	 * @return the Hamming distance between two solutions
	 */
	public static int hammingDistance(boolean[] sol1, boolean[] sol2) {
		int distance = 0;
		for(int i=0; i<sol1.length; i++) {
			if(sol1[i]!=sol2[i]) distance++; 
		}
		return distance;
	}
	
	/**
	 * Recombine tries to generate offspring from to solution parents. It tries
	 * to preserve information by setting those nodes which appear in the same partition
	 * in the parents also in the same partition in the child.
	 * The other nodes are randomly assigned a 1 or a 0 (without exceeding the max number of 250)
	 * @param parent1 the first parent used for recombination
	 * @param parent2 the second parent used for recombination
	 * @return the child solution, which is a combination of both parents and some degree of randomness
	 */
	public static Solution recombine(Solution parent1, Solution parent2) {
		// ensure that the ones and zeros represent that same partitions (i.e. if the parents are
		// more than 250 switches apart, invert on of the parents. 
		if(hammingDistance(parent1.getSol(), parent2.getSol()) > 250) parent1.invert() ;
		
		int count0 = 0;
		int count1 = 0;
		int[] bits = new int[500]; 
		
		// Get bitstrings for parents and initialise child
		boolean[] b1 = parent1.getSol();
		boolean[] b2 = parent2.getSol();
		boolean[] b0 = new boolean[500];
		
		// copies the bits that are equal for both solutions
		for(int i=0; i<500; i++) {
			if(!b1[i]&&!b2[i]){
				bits[i] = 0; 
				count0++;
			}else {
				if(b1[i]&&b2[i]){
					bits[i] = 1; 
					count1++;
				}else { 
					bits[i] = 2;
				}
			}
		}

		// randomly assigns left nodes to both partitions
		for(int i=0; i<500; i++) {
			if(bits[i]==0){
				b0[i] = false;
			}else if(bits[i]==1){
				b0[i] = true;
			}else {
				if((count0<250)&&(count1<250)) {
					// if there are not enough ones and zeros pick one randomly
					b0[i] = random.nextBoolean();
				} else {
					b0[i] = (count0==250);
				}
				if (b0[i]) count1++; else count0++;
			}
		}
		return new Solution(b0,type);
	}
	
	/**
	 * quickSort sorts an array of solutions based on their cutsize.
	 * It sorts the solutions in an ascending order (lowest cutsize first)
	 * @param pop the array with solutions that need to be sorted
	 * @return the sorted array with solution
	 */
	public static Solution[] quickSort(Solution[] pop) {
		
		ArrayList<Solution> pop2 = new ArrayList<Solution>();
		for(int i=0; i<pop.length; i++) {
			pop2.add(pop[i]);
		}
		pop2 = quickSort(pop2);
		for(int i=0; i<pop.length; i++) {
			pop[i] = pop2.get( (pop2.size()-1) - i);
		}
		return pop;
	}
	
	/**
	 * quicksort sorts an arraylist with solutions based on their cutsize.
	 * It sorts the arraylist in an descending order (highest first)
	 * @param pop the arraylist that needs to be sorted
	 * @return the sorted arraylist with solutions (descending cutsize)
	 */
	public static ArrayList<Solution> quickSort(ArrayList<Solution> pop){
        int length = pop.size();
        if(length<2){
                return pop;
        }
        else{
            long pivot;
            int ind = length/2;
            
            ArrayList<Solution> L = new ArrayList<Solution>();
            ArrayList<Solution> R = new ArrayList<Solution>();
            ArrayList<Solution> sorted = new ArrayList<Solution>();
            Solution solution = (Solution) pop.get(ind);
            pivot = solution.getCutsize();
            
            for(int i=0;i<length;i++){
                if(i!=ind){
                    Solution sol = (Solution) pop.get(i);
                    long fitness = sol.getCutsize();
                    
                    if(fitness > pivot){
                        L.add(sol);
                    } else if (fitness < pivot){
                        R.add(sol);
                            
                    } else { // fitness == pivot
                        L.add(sol);
                    }
                }
	        } // end for loop
            
	         L = quickSort(L);
	         R = quickSort(R);
	     
	                
	         //----
	         sorted.addAll(L);
	         sorted.add(solution);
	         sorted.addAll(R);
	         
	         return sorted;
         }
	}
	
	/**
	 * insert tries to insert a new solution into the current solution array
	 * It inserts it in a sorted way, where the solutions are sorted ascending by
	 * their cutsize. Since the length of the solution does not change, the weakest entry
	 * disappears from the array
	 * @param sols the current array of solutions
	 * @param sol the solution that needs to be inserted into the solution.
	 * @return
	 */
	private static Solution[] insert(Solution[] sols, Solution sol){
		ArrayList<Solution> solus = new ArrayList<Solution>();
		for (int i=0;i<sols.length;i++){
			solus.add(sols[i]);
		}
		solus = insert(solus,sol);
		for (int i=0;i<sols.length;i++){
			sols[i]=solus.get(i);
		}
		return sols;
	}
	/**
     * insert handles an arrayList of solutions, and inserts a new solution into the
     * arrayList is a sorted manner. The arrayList is sorted ascending based on the cut
     * size of the solutions. This is the startup class.
     * @param sol the solution to be added to the arrayList
     * @param sols the arraylist with solutions
     * @returns the sorted arrayList with solutions, including sol
     */
    private static ArrayList<Solution> insert(ArrayList<Solution> sols, Solution sol){
        int mid = sols.size()/2;
        if (sol.getCutsize() <= ((Solution) sols.get(mid)).getCutsize()){
            // add in left halve
            return insert(sol,0,mid,sols);
        } else {
            return insert(sol,mid,sols.size(),sols);
        }
    }
       
    /**
     * Iterative function that sorts the solution arrayList accordingly.
     * @param sol
     * @param left
     * @param right
     */
    private static ArrayList<Solution> insert(Solution sol, int left, int right,ArrayList<Solution> sols){
        if (right - left >= 2){                        
            int mid = (right + left)/2;
            if (sol.getCutsize() < ((Solution)sols.get(mid)).getCutsize()){
                return insert(sol,left,mid,sols);
            } else {
                return insert(sol,mid,right,sols);
            }
        } else {
        	//System.out.println("Old pop: "+population.toString());
            if (sol.getCutsize() <= ((Solution) sols.get(left)).getCutsize()){
                sols.add(left, sol);
            } else {
                sols.add(right,sol);
                
            }

            sols.remove(sols.size()-1);
            //System.out.println("New pop: "+population.toString());
            //System.out.println("Change at: "+left+" - "+right);
        }
        return sols;
    }

    /** Get CPU time in nanoseconds. */
    public static long getCpuTime( ) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        return bean.isCurrentThreadCpuTimeSupported( ) ?
            bean.getCurrentThreadCpuTime( ) : 0L;
    }
     
    /** Get user time in nanoseconds. */
    public static long getUserTime( ) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        return bean.isCurrentThreadCpuTimeSupported( ) ?
            bean.getCurrentThreadUserTime( ) : 0L;
    }

    /** Get system time in nanoseconds. */
    public static long getSystemTime( ) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        return bean.isCurrentThreadCpuTimeSupported( ) ?
            (bean.getCurrentThreadCpuTime( ) - bean.getCurrentThreadUserTime( )) : 0L;
    }
    

}

/**
 * Helper class Answer stores the answers obtained by running the MLS
 * ILS and GLS algorithms
 * @author Julius
 *
 */
class Answer{
	protected Solution solution;
	protected int nrOfSwaps;
	protected long time;
	protected int[] best=new int[2];
	
	/**
	 * Constructor that instantiates the the instance
	 * @param sol the solution
	 * @param swaps total nr of swaps
	 * @param cpuTime the amount of time needed to get the solution
	 */
	public Answer(Solution sol, int swaps, long cpuTime){
		solution = sol;
		nrOfSwaps = swaps;
		time = cpuTime;
	}
}
