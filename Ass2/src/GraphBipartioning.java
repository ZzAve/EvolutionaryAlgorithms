import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class GraphBipartioning {
	
	static Random random;
	static Node[] nodes;
	static int numRuns;
	static Statistics stat;
	
	
	//static int popsize = 1;
	//static boolean[][] population = new boolean[popsize][500];
	
	public static void main(String[] args) throws IOException {
		
		random = new Random();
		nodes = new Node[500];
		numRuns = 30;
		stat = new Statistics();
	
		
		// create all nodes
		parse();
		
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
			localsMLS[i] = multiLS(1000);
			localsILS[i] = iteratedLS(5); // include perturbation size (2,3,4,5,... ?)
			localsGLS[i] = geneticLS(50); // or 100
		}
		
		medianMLS = stat.median(numRuns, localsMLS);
		meanMLS = stat.mean(numRuns, localsMLS);
		medianILS = stat.median(numRuns, localsILS);
		meanILS = stat.mean(numRuns, localsILS);
		medianGLS = stat.median(numRuns, localsGLS);
		meanGLS = stat.mean(numRuns, localsGLS);

		double varMLS = stat.variance(numRuns, localsMLS, meanMLS);
		double varILS = stat.variance(numRuns, localsILS, meanILS);
		double varGLS = stat.variance(numRuns, localsGLS, meanGLS);
		
		//double tTest = stat.tTest(meanILS, meanGLS, varILS, varGLS, numRuns, numRuns);
		
		System.out.println("meanMLS = " + meanMLS + ", medianMLS = " + medianMLS);
		System.out.println("meanILS = " + meanILS + ", medianILS = " + medianILS);
		System.out.println("meanGLS = " + meanGLS + ", medianGLS = " + medianGLS);
	}
	
	/**
	 * The Multi-Start Local Search algorithm
	 * @return
	 */
	public static int multiLS(int nrOfStarts) {
		int best = 1000;
		Solution solution;
		for (int search=0;search<nrOfStarts;search++){
			// generate solution
			solution = new Solution();
			
			//apply local search
			solution = localSearch(solution);
			if(solution.getCutsize() < best) best = solution.getCutsize();
		}		
		return best;
	}

	
	public static int iteratedLS(int perturb) {
				
		Solution solution = new Solution();
		Solution oldSolution = new Solution();
		oldSolution.setCutsize(1000);

		solution = localSearch(solution);
		while (solution.getCutsize() < oldSolution.getCutsize()){
			oldSolution = solution;
			
			// Create new solution, which is a perturb solution of the old optimum
			solution = new Solution(oldSolution.getSol(),oldSolution.getCutsize());
			solution.perturbation(perturb);
			
			//Apply local search on new solution
			solution = localSearch(solution);
		}
		
		return solution.getCutsize();
	}
	
	public static int geneticLS(int popsize) {
		
		Solution[] solutions = new Solution[popsize];
		
		int best = 1000;
		int[] worst = new int[2];
		worst[0] = 0;
		
		// generate population and apply local search
		for(int i=0; i < popsize; i++) {
			solutions[i] = new Solution();			
			solutions[i] = localSearch(solutions[i]);
			
			// keep track of worst solution
			if(solutions[i].getCutsize() > worst[0]) {
				worst[0] = solutions[i].getCutsize();
				worst[1] = i;
			}
		}
		
		solutions = quickSort(solutions);
		// recombine and mutate
		for(int j=0; j<1000; j++) {
			int rand1 = random.nextInt (popsize);
			int rand2 = random.nextInt (popsize);
			
			
			
			Solution temp = new Solution( recombine(solutions[rand1].getSol(), 
					 								solutions[rand2].getSol() ) );
			temp = localSearch(temp);
			
			// compare to worst and if better, replace
			if(temp.getCutsize() < worst[0]) {
				solutions[worst[1]]= temp;
			}
			
			for(int i=0; i < popsize; i++) {
				if(solutions[i].getCutsize() > worst[0]) {
					worst[0] = solutions[i].getCutsize();
					worst[1] = i;
				}
			}
		}
		
		for(int i=0; i < popsize; i++) {
			if(solutions[i].getCutsize() < best) {
				best = solutions[i].getCutsize();
			}
		}
		
		return best;
	}
	
		public static Solution localSearch(Solution solution) {
			// swap bits and stop local search when no improvement found
			while(solution.swap()){/*keep swapping until no improvement is found anymore */}
		
			return solution;
		}
		
	
	
	public static void parse() throws IOException {
		
		FileInputStream stream = new FileInputStream("U500.05.txt");
		//FileInputStream stream = new FileInputStream("G500.005.txt");
		DataInputStream in = new DataInputStream(stream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
               
        // Read lines and add nodes
        String line;
        int counter = 0;
        while( (line = br.readLine()) != null){
        	nodes[counter] = new Node(line);
        }
        
		return;
	}

	public static int hammingDistance(boolean[] sol1, boolean[] sol2) {
		
		int distance = 0;
		
		for(int i=0; i<sol1.length; i++) {
			if(sol1[i]!=sol2[i]) distance++; 
		}
		
		return distance;
	}
	
	public static boolean[] invert(boolean[] sol) {
		
		boolean[] invert = new boolean[sol.length];
		
		for(int i=0; i<sol.length; i++) {
			invert[i] = !sol[i];
		}
		
		return invert;
	
	}
	
	
	
	public static boolean[] perturbation(boolean[] sol) {
		
		int rand0 = 0;
		int rand1 = 0;
		
		// find random nodes that are in two different parts
		while(true) {
			int i = random.nextInt (sol.length);
			
			if(!sol[i]) {
				rand0 = i;
				break;
			}
		}
		
		while(true) {
			int i = random.nextInt (sol.length);
			
			if(sol[i]) {
				rand1 = i;
				break;
			}
		}
		
		boolean temp = sol[rand0];
        sol[rand0] = sol[rand1];
        sol[rand1] = temp;
		
        return sol;
	}

	

	
	public static boolean[] recombine(boolean[] b1, boolean[] b2) {
		
		boolean[] b0 = new boolean[500];
		
		if(hammingDistance(b1, b2) > 250) invert(b1) ;
		boolean b = random.nextBoolean();
		int count0 = 0;
		int count1 = 0;
		
		int[] bits = new int[500]; 
		
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
			if(bits[i]==0) b0[i] = false;
			else {
				if(bits[i]==1) b0[i] = true;
				else {
					if((count0<250)&&(count1<250)) {
						b0[i] = b;
						if(b) {count1++;}
						else {count0++;}
						b = random.nextBoolean();
					}
					else {
						if(count0==250) b0[i] = true;
						else {if(count1==250) b1[i] = false;}
					}
				}
			}
		}
			
		return b0;
	
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
}