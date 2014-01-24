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
		type = 1;			// 1 = Ugraph, 2 = Ggraph
		
		// create all nodes
		parse(type);			
		

		Answer[] localsMLS = new Answer[numRuns];
		Answer[] localsILS = new Answer[numRuns];
		Answer[] localsGLS = new Answer[numRuns];
		double medianMLS = 0;
		double meanMLS = 0;
		double medianILS = 0;
		double meanILS = 0;
		double medianGLS = 0;
		double meanGLS = 0;
	
		for(int i=0; i<numRuns; i++) {
			System.out.println("Round "+(i+1)+"/"+numRuns+")");
			// entry zero returns the minimum cutsize, entry one the number of vertex swaps;
			localsMLS[i] = multiLS(300);
			localsILS[i] = iteratedLS(3); // include perturbation size (2,3,4,5,... ?)
			localsGLS[i] = geneticLS(50); // or 100
		}
		
		medianMLS = Statistics.median(localsMLS);
		meanMLS = Statistics.mean(localsMLS);
		medianILS = Statistics.median(localsILS);
		meanILS = Statistics.mean(localsILS);
		medianGLS = Statistics.median(localsGLS);
		meanGLS = Statistics.mean(localsGLS);

		double varMLS = Statistics.variance(localsMLS);
		double varILS = Statistics.variance(localsILS);
		double varGLS = Statistics.variance(localsGLS);
		
		//double tTest = stat.tTest(meanILS, meanGLS, varILS, varGLS, numRuns, numRuns);
		
		System.out.println("meanMLS = " + meanMLS + ", medianMLS = " + medianMLS);
		System.out.println("meanILS = " + meanILS + ", medianILS = " + medianILS);
		System.out.println("meanGLS = " + meanGLS + ", medianGLS = " + medianGLS);
		
		//write data to file
		/*System.out.println("Beginnen met schrijven");
		try {
	        BufferedWriter out = new BufferedWriter(new FileWriter("results.txt"),32678);
	        String settingsString = "SolutionLength,popsize,toursize,fitfunc,linkage,probCross,crossType,";
	        String settings;
	        for (int i=0; i<result1.size();i++){
	        	settings="";
	        	for (int j=0;j<((double[]) result1.get(i)).length;j++){
	        		settings+=((double[])result1.get(i))[j]+",";
	        	}	        	
	        	out.write(settingsString);
	        	out.newLine();
	        	out.write(settings);
	        	out.newLine();
	        	
	        	ArrayList paramList = ((ArrayList)result2.get(i));
	        	for (int k=0;k<paramList.size();k++){
	        		for (int l=0;l<((int[]) paramList.get(k)).length;l++){
	        			out.write(((int[])paramList.get(k))[l]+",");
	        		}
	        		out.newLine();
	        	}
	        }
	        out.close();
	    } catch (IOException e) {
	    	System.err.println("FileNotFoundException: " + e.getMessage());
	    }*/
		
	}
	
	/**
	 * The Multi-Start Local Search algorithm
	 * @return
	 */
	public static Answer multiLS(int nrOfStarts) {
		long startCPUtime = getCpuTime();
		Solution best = new Solution(new boolean[500],1000,type);
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
		
		return new Answer(best,nrOfSwaps,getCpuTime() - startCPUtime);
	}

	
	public static Answer iteratedLS(int perturb) {
		long startCPUtime = getCpuTime();
		System.out.print("\t Perfroming iteratedLS ");	
		int nrOfSwaps = 0; int threshold = 1500; int count=threshold;
		Solution solution = new Solution(type);
		Solution oldSolution = new Solution(type);
		oldSolution.setCutsize(1000);
						
		//Apply local search on new solution
		nrOfSwaps+= solution.localSearch();
		while (solution.getCutsize() < oldSolution.getCutsize()){
			if(nrOfSwaps-threshold>count){System.out.print(". ");count+=threshold;}
			oldSolution = solution;
			
			// Create new solution, which is a perturb solution of the old optimum
			solution = new Solution(oldSolution.getSol(),oldSolution.getCutsize(), type);
			solution.perturbation(perturb);
			
			//Apply local search on new solution
			nrOfSwaps+= solution.localSearch();
		}
		System.out.println();
		System.out.println("\t\t Total number of swaps: "+nrOfSwaps);
		
		
		return new Answer(oldSolution,nrOfSwaps,getCpuTime() - startCPUtime);
	}
	
	public static Answer geneticLS(int popsize) {
		long startCPUtime = getCpuTime();
		System.out.print("\t Performing geneticLS ");
		Solution[] solutions = new Solution[popsize];
		int nrOfSwaps =0; int threshold=1000; int count=threshold;
		
		// generate population and apply local search
		for(int i=0; i < popsize; i++) {
			if(nrOfSwaps-threshold>count){System.out.print(". ");count+=threshold;}
			solutions[i] = new Solution(type);			
			nrOfSwaps += solutions[i].localSearch();
		}
		
		/*System.out.print("Before sorting <");
		for( int i=0;i<solutions.length;i++){
			System.out.print(solutions[i].getCutsize()+",");
		}
		System.out.println(">");*/
		// sort the solutions
		solutions = quickSort(solutions);
		/*System.out.print("After sorting <");
		for( int i=0;i<solutions.length;i++){
			System.out.print(solutions[i].getCutsize()+",");
		}
		System.out.println(">");*/
		
		int rand1,rand2;
		// recombine and mutate
		boolean changed = true;
		while (changed){
			if(nrOfSwaps-threshold>count){System.out.print(". ");count+=threshold;}
			//get two parents
			rand1 = random.nextInt(popsize);
			while ((rand2 = random.nextInt(popsize))==rand1){/*repeat until unique number is found*/}
			
			// Create the child, and perform localsearch to find a local optimum
			Solution temp = recombine(solutions[rand1],solutions[rand2]);
			nrOfSwaps += temp.localSearch();
			
			// compare to worst and if better, replace
			changed = temp.getCutsize() < solutions[solutions.length-1].getCutsize();
			if(changed){
				solutions = insert(solutions,temp);
				//System.out.println("change! " +temp.getCutsize() );
			}

		}		
		System.out.println();
		System.out.println("\t\t Total number of swaps: "+nrOfSwaps);
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
		if(type==1) {
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
            int pivot;
            int ind = length/2;
            
            ArrayList<Solution> L = new ArrayList<Solution>();
            ArrayList<Solution> R = new ArrayList<Solution>();
            ArrayList<Solution> sorted = new ArrayList<Solution>();
            Solution solution = (Solution) pop.get(ind);
            pivot = solution.getCutsize();
            
            for(int i=0;i<length;i++){
                if(i!=ind){
                    Solution sol = (Solution) pop.get(i);
                    int fitness = sol.getCutsize();
                    
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
     * 
     * @param sol
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
     * 
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
    public long getUserTime( ) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        return bean.isCurrentThreadCpuTimeSupported( ) ?
            bean.getCurrentThreadUserTime( ) : 0L;
    }

    /** Get system time in nanoseconds. */
    public long getSystemTime( ) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        return bean.isCurrentThreadCpuTimeSupported( ) ?
            (bean.getCurrentThreadCpuTime( ) - bean.getCurrentThreadUserTime( )) : 0L;
    }
    

}

class Answer{
	protected Solution solution;
	protected int nrOfSwaps;
	protected long time;
	
	public Answer(Solution sol, int swaps, long cpuTime){
		solution = sol;
		nrOfSwaps = swaps;
		time = cpuTime;
	}
}
