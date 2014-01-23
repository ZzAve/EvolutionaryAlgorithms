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
		
		int[] localsMLS = new int[numRuns];
		int[] localsILS = new int[numRuns];
		int[] localsGLS = new int[numRuns];
		double medianMLS = 0;
		double meanMLS = 0;
		double medianILS = 0;
		double meanILS = 0;
		double medianGLS = 0;
		double meanGLS = 0;
	
		for(int i=0; i<numRuns; i++) {
			// entry zero returns the minimum cutsize, entry one the number of vertex swaps;
			localsMLS[i] = multiLS(1000)[0];
			//localsILS[i] = iteratedLS(5)[0]; // include perturbation size (2,3,4,5,... ?)
			//localsGLS[i] = geneticLS(50)[0]; // or 100
		}
		
		medianMLS = Statistics.median(numRuns, localsMLS);
		meanMLS = Statistics.mean(numRuns, localsMLS);
		medianILS = Statistics.median(numRuns, localsILS);
		meanILS = Statistics.mean(numRuns, localsILS);
		medianGLS = Statistics.median(numRuns, localsGLS);
		meanGLS = Statistics.mean(numRuns, localsGLS);

		double varMLS = Statistics.variance(numRuns, localsMLS, meanMLS);
		double varILS = Statistics.variance(numRuns, localsILS, meanILS);
		double varGLS = Statistics.variance(numRuns, localsGLS, meanGLS);
		
		//double tTest = stat.tTest(meanILS, meanGLS, varILS, varGLS, numRuns, numRuns);
		
		System.out.println("meanMLS = " + meanMLS + ", medianMLS = " + medianMLS);
		System.out.println("meanILS = " + meanILS + ", medianILS = " + medianILS);
		System.out.println("meanGLS = " + meanGLS + ", medianGLS = " + medianGLS);
	}
	
	/**
	 * The Multi-Start Local Search algorithm
	 * @return
	 */
	public static int[] multiLS(int nrOfStarts) {
		int best = 1000;
		int nrOfSwaps =0;
		Solution solution;
		System.out.print("Perfroming multiLS ");
		for (int search=1;search<=nrOfStarts;search++){
			if (search%50==0) System.out.print(". ");
			if (search%250==0) System.out.print(search+" ");
			
			// generate solution
			solution = new Solution(type);
			
			//apply local search
			nrOfSwaps += solution.localSearch();
			if(solution.getCutsize() < best) best = solution.getCutsize();
		}	
		System.out.println();
		System.out.println("Total number of swaps for "+nrOfStarts+" starts: "+nrOfSwaps);
		return (new int[] {best,nrOfSwaps});
	}

	
	public static int[] iteratedLS(int perturb) {
				
		int nrOfSwaps = 0;
		Solution solution = new Solution(type);
		Solution oldSolution = new Solution(type);
		oldSolution.setCutsize(1000);
		

		nrOfSwaps+= solution.localSearch();
		while (solution.getCutsize() < oldSolution.getCutsize()){
			oldSolution = solution;
			
			// Create new solution, which is a perturb solution of the old optimum
			solution = new Solution(oldSolution.getSol(),oldSolution.getCutsize(), type);
			solution.perturbation(perturb);
			
			//Apply local search on new solution
			nrOfSwaps+= solution.localSearch();
		}
		
		return (new int[] {solution.getCutsize(),nrOfSwaps});
	}
	
	public static int[] geneticLS(int popsize) {
		
		Solution[] solutions = new Solution[popsize];
		int nrOfSwaps =0;
		
		// generate population and apply local search
		for(int i=0; i < popsize; i++) {
			solutions[i] = new Solution(type);			
			nrOfSwaps += solutions[i].localSearch();
			
		}
		
		// sort the solutions
		solutions = quickSort(solutions);
		 
		int rand1,rand2;
		// recombine and mutate
		boolean changed = true;
		while (changed){
			rand1 = random.nextInt(popsize);
			rand2 = random.nextInt(popsize);
			
			Solution temp = recombine(solutions[rand1],solutions[rand2]);
			nrOfSwaps += temp.localSearch();
			
			// compare to worst and if better, replace
			changed = temp.getCutsize() < solutions[solutions.length-1].getCutsize();
			if(changed){
				solutions = insert(solutions,temp);
			}
		}
		
		
		return (new int[] {solutions[0].getCutsize(),nrOfSwaps});
	}
		
	
	/**
	 * 
	 * @param type
	 * @throws IOException
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
	 * @return the hamming distance between two solutions
	 */
	public static int hammingDistance(boolean[] sol1, boolean[] sol2) {
		
		int distance = 0;
		
		for(int i=0; i<sol1.length; i++) {
			if(sol1[i]!=sol2[i]) distance++; 
		}
		
		return distance;
	}
	
	/**
	 * 
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	public static Solution recombine(Solution parent1, Solution parent2) {
		
		boolean[] b0 = new boolean[500];
		
		if(hammingDistance(parent1.getSol(), parent2.getSol()) > 250) parent1.invert() ;
		boolean b = random.nextBoolean();
		int count0 = 0;
		int count1 = 0;
		
		int[] bits = new int[500]; 
		boolean[] b1 = parent1.getSol();
		boolean[] b2 = parent2.getSol();
		// copies the bits that are equal for both solutions
		for(int i=0; i<500; i++) {
			if(!b1[i]&&!b2[i]) {bits[i] = 0; count0++;}
			else {
				if(b1[i]&&b2[i]) {bits[i] = 1; count1++;}
				else { 
					bits[i] = 2;
				}
			}
		}

		// randomly assigns left nodes to both partitions
		for(int i=0; i<500; i++) {
			if(bits[i]==0){
				b0[i] = false;
			}else {
				if(bits[i]==1){
					b0[i] = true;
				}else {
					if((count0<250)&&(count1<250)) {
						b0[i] = b;
						if(b) count1++; else count0++;
						b = random.nextBoolean();
					} else {
						b0[i] = (count0==250);
						if (b0[i]) count1++; else count0++;
					}
				}
			}
		}
		return new Solution(b0,type);
	
	}
	
	
	public static Solution[] quickSort(Solution[] pop) {
		
		ArrayList<Solution> pop2 = new ArrayList<Solution>();
		for(int i=0; i<pop.length; i++) {
			pop2.add(pop[i]);
		}
		pop2 = quickSort(pop2);
		for(int i=0; i<pop.length; i++) {
			pop[i] = pop2.get(i);
		}
		return pop;
	}
	
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

}
