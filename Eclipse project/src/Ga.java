import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Class GA
 * @author Julius
 *
 */
public class Ga{
	
	private ArrayList population;
	
	private int solLength;
	private int popSize;
	private int tourSize;
	private int fitnessFunction;
	private int linkage;			// 1=tight, 2=random
	private double probCross;				// probability of crossover (else mutation) 
	private int crossType;					// 1=2point, 2=uniform
	private int nrOfGenerations;	// at most 10*popSize 
	private int bestFitness;		// current best fitness of population
	private int maxFitness;			// highest fitness value possible (given fitness function)
	Random bitGenerator;
	int unchanged;				// keeps track of change in population
	
	public Ga(int solutionLength, int populationSize, int tournamentSize, int fitnessFunctionType, int linkageType, double probCrossover, int crossoverType){
		
		solLength = solutionLength;
		popSize = populationSize;
		tourSize = tournamentSize;
		fitnessFunction = fitnessFunctionType;
		linkage = linkageType;
		probCross = probCrossover;
		crossType = crossoverType;
		nrOfGenerations = 10*popSize;
		bestFitness = 0;
		maxFitness = Solution.getMaxFitness(fitnessFunction);
		bitGenerator = new Random();
		
		unchanged = 0;
		population = new ArrayList();
	}
	
	
	private void generatePopulation(){
		
		// instantiate a random population (with x solutions)
		
	    int[] dummySolution = new int[solLength];
		for (int solNr=0;solNr<popSize;solNr++){
			for (int bitNr=0; bitNr<solLength;bitNr++ ){
				dummySolution[bitNr] = bitGenerator.nextInt(2);
			}
			
			population.add(new Solution(dummySolution,fitnessFunction,linkage));
		}
		
		System.out.print("Fitnesses before: ");
		for(int solNr=0;solNr<popSize;solNr++) {
			Solution sol = (Solution) population.get(solNr);
			System.out.print(sol.getFitness()+", ");
		}
		System.out.println();
		System.out.print("Sorting: ");
		population = quickSort(population);
		System.out.println();
		System.out.print("Fitnesses after: ");
		for(int solNr=0;solNr<popSize;solNr++) {
			Solution sol = (Solution) population.get(solNr);
			System.out.print(sol.getFitness()+", ");
		}
		System.out.println();
		Solution best = (Solution) population.get(0);
		bestFitness = (int) best.getFitness();
	}
	
	
	private ArrayList performTournament2(int tournamentSize,int requestedNr){
		ArrayList winners = new ArrayList();
		Random randomNr = new Random();
		switch (tournamentSize){
		case 1: 
			//random
			for (int i=0;i<requestedNr;i++){
				winners.add(population.get(randomNr.nextInt(population.size())));
			}
			break;
		case 2:
			// let two units fight
			for (int i=0;i<requestedNr;i++){
				int sol1, sol2;
				sol1 = randomNr.nextInt(solLength);
				sol2 = randomNr.nextInt(solLength);
				while (sol1 == sol2){
					sol2 = randomNr.nextInt(solLength);
				}
				if (((Solution) population.get(sol1)).getFitness() >= 
					    ((Solution) population.get(sol2)).getFitness()){
					// one wins or draw
					winners.add(population.get(sol1));
				} else {
					winners.add(population.get(sol2));
				}
			}
			break;
		default:
			break;
		}
		return winners;
	}
	/**
	 * 
	 * @param tournamentSize
	 * @return
	 */
	private ArrayList performTournament(int tournamentSize){
		System.out.print(" performing tournament ("+tournamentSize+")");
		ArrayList winners = new ArrayList();
		switch (tournamentSize){
		  case 1:
			  //System.out.print(" random!");
			  Random randomNr = new Random();
			  for (int poolEntry=0;poolEntry<popSize;poolEntry++){
				 winners.add(population.get(randomNr.nextInt(population.size())));
			  }
			  break;
		  case 2:
			  ArrayList tournPop = (ArrayList) population.clone();
			  for (int round=1;round<3;round++){
				//System.out.println("The population before shuffling:");
				//System.out.println(tournPop.toString());
				Collections.shuffle(tournPop);
				//System.out.println("The population after shuffling:");
				//System.out.println(tournPop.toString());
				
				for (int fighter1=0; fighter1<popSize-1; fighter1+=2){
					if (((Solution)tournPop.get(fighter1)).getFitness() <= 
						   ((Solution)tournPop.get(fighter1+1)).getFitness()){
						//draw means fighter2 wins
						winners.add(tournPop.get(fighter1+1));
					} else {
						winners.add(tournPop.get(fighter1));
					}
				}
			  }
			  break;
		  default:
			  break;
		}

		System.out.print("Tour fitnesses: ");
		for(int i=0; i<winners.size(); i++) {
			System.out.print(((Solution)winners.get(i)).getFitness()+ ", ");
		}
		System.out.println();
		return winners;
	}
	
	private ArrayList crossOver(Solution parent1,Solution parent2){
		int[] child1 = new int[solLength];
		int[] child2 = new int[solLength];
		ArrayList children = new ArrayList();
		
		for(int bit=0; bit<solLength; bit++) {
			int rand = bitGenerator.nextInt(2);
			if(rand==0) {
				child1[bit] = parent1.getSolution()[bit];
				child2[bit] = parent2.getSolution()[bit];
			}
			else {
				child1[bit] = parent2.getSolution()[bit];
				child2[bit] = parent1.getSolution()[bit];
			}
		}
		
		// Create two new solutions, whose bitstring is given by the just calculated one
		children.add(new Solution(child1, fitnessFunction, linkage));
		children.add(new Solution(child2, fitnessFunction, linkage));
		
		/*if (sol1.getFitness() >= sol1.getFitness()){
			return sol1;
		} else {
			return sol2;
		}*/
		
		
		System.out.print(" Crossover: p1:"+parent1.getFitness()+" p2:"+parent2.getFitness());
		System.out.print(" Child1:"+((Solution) children.get(0)).getFitness()+" child2:"+((Solution) children.get(1)).getFitness());
		return children;
	}
	
	private Solution mutation(Solution sol){
		Solution mutatedSol = new Solution(sol.getSolution(),fitnessFunction,linkage);
		Random randomNr = new Random();
		int nrOfMutations=0;
		
		while (randomNr.nextFloat() <0.5 ){
			nrOfMutations++;
		}
		if (nrOfMutations>0){
			//Pick  random numbers!
			int[] mutations = new int[nrOfMutations];
			mutations[0]=randomNr.nextInt(100);
			int nb_picked = 1;
			int index;
			boolean found;
			while (nb_picked < nrOfMutations){
			   index = randomNr.nextInt(100);
			   found=false;
			   for(int i=0;i<nb_picked;i++){
				  if(mutations[i]==index){
				    	found=true;
				    	break;
				  }
			   }
			   if(!found){
				  mutations[nb_picked]=index;
				  nb_picked++;
			   }
			}
			mutatedSol.mutateSolution(mutations);
		}
		
		System.out.print(" Mutation: p:"+sol.getFitness()+" child:"+mutatedSol.getFitness());
		return mutatedSol;
		
	}
		
	/**
	 * 
	 * @param pop
	 * @return
	 */
	public ArrayList quickSort(ArrayList pop){
		int length = pop.size();
		if(length<2){
			return pop;
		}
		else{
			int pivot;
			int ind = length/2;
			
			ArrayList L = new ArrayList();
			ArrayList R = new ArrayList();
			ArrayList sorted = new ArrayList();
			Solution solution = (Solution) pop.get(ind);
			pivot = solution.getFitness();
			
			for(int i=0;i<length;i++){
				if(i!=ind){
					Solution sol = (Solution) pop.get(i);
					int fitness = sol.getFitness();
					
					if(fitness > pivot){
						L.add(sol);
					} else if (fitness < pivot){
						R.add(sol);
						
					} else { // fitness == pivot
						if (sol.getId() > solution.getId()){
							L.add(sol);
						} else {
							R.add(sol);
						}
					}
				}
			} // end for loop
			System.out.print(" Going deeper once more ");
			L = quickSort(L);
			R = quickSort(R);
		 
			
		 //----
		 sorted.addAll(L);
		 sorted.add(solution);
		 sorted.addAll(R);
		 
		 return sorted;
		 }
	}

	private void insert(Solution sol){
		int mid = population.size()/2;
		if (sol.getFitness() >= ((Solution) population.get(mid)).getFitness()){
			// add in right halve
			insert(sol,mid,population.size());
		} else {
			insert(sol,0,mid);
		}
	}
		
	private void insert(Solution sol, int left, int right){
		if (right - left > 2){			
			int mid = (right + left)/2 +1;
			if (sol.getFitness() >= ((Solution)population.get(mid)).getFitness()){
				insert(sol,mid,right);
			} else {
				insert(sol,left,mid);
			}
		} else {
			if (sol.getFitness() >= ((Solution) population.get(left)).getFitness()){
				population.add(left, sol);
			} else {
				population.add(right,sol);
			}
			population  = (ArrayList) population.subList(0, 99);
		}
	}
	public ArrayList runGa(){
		
		// result consists of number of optimum found, number of generations (and maybe highest fitness)
		int numOpt = 0;
		int numGen = 0;
		ArrayList result = new ArrayList();
		
		for(int run=0; run<1; run++){
			System.out.print("Run "+run+ "(");
			Solution.resetIds();
			generatePopulation();
			unchanged=0;
			
			int gen = runGa(0);
			// update numOpt and numGen
			
			
		}
		//result.add(numOpt);
		//result.add(numGen);

		return result;
	}
	
	private ArrayList runGaa(int trialNr, ArrayList answer){
		ArrayList winners;
		if (trialNr<50){
			// set up
			
			Solution.resetIds();
			generatePopulation();
			unchanged=0;
			int evaluations=0;
			
			// run until...
			while ((unchanged < nrOfGenerations) && (bestFitness < maxFitness)){
				// select
				winners = performTournament(tourSize);
				
				// crossover or mutate
		 		if (crossType == 0){
	 			    // mutation
	 	 			// compute fitness and add to childPool
	 	 			for (int i=0; i<winners.size();i++) {
	 	 				winners.set(i, mutation((Solution) winners.get(i)));
					}
		 		} else if (crossType == 0.5){ 
 	   				if (bitGenerator.nextFloat() <0.5){
 	   					// do crossover
 	   					winners = crossOver((Solution)winners.get(0),(Solution)winners.get(1));	   				
	 	   			} else{
	 	   				// do mutation
		 	   			for (int i=0; i<winners.size();i++) {
		 	 				winners.set(i, mutation((Solution) winners.get(i)));
						}
	 	   			}
		 		} else {
		 			winners = crossOver((Solution)winners.get(0),(Solution)winners.get(1));
		 		}
		 		
		 		// Get best solution
		 		Solution bestSol = (Solution) winners.get(0);
		 		for (int i=1; i<winners.size();i++){
		 			if (((Solution) winners.get(i)).getFitness() > bestSol.getFitness()){
		 				bestSol = (Solution) winners.get(i);
		 			}
		 		}
		 		
				// sorted insert
		 		if (bestSol.getFitness() >= ((Solution) population.get(population.size()-1)).getFitness()){
		 			insert(bestSol);
		 			unchanged=0;
		 		} else{
		 			unchanged++;
		 		}
		 		
				// add 1 to counter
		 		evaluations++;
			}
			
			
			
			
			return runGaa(trialNr+1, answer);
		} else {
			return answer;
		}
	}
	
	private int runGa(int nrOfGen){
		System.out.print("| Gen: "+nrOfGen);
		//+ unchanged condition for 10*popSize runs
		System.out.print(" unchanged < nrOfGenerations "+unchanged + " < "+ nrOfGenerations);
		System.out.println("|| bestFitness < maxFitness " + bestFitness + " < "+maxFitness);
		
		if ((unchanged < nrOfGenerations) && (bestFitness < maxFitness)) {
			ArrayList parentPool = performTournament(tourSize);
		    ArrayList childPool = new ArrayList(200);
		    childPool.addAll(parentPool);
		    System.out.println();
		    System.out.println("The parentpool: "+parentPool.toString());
		    
		    // puur omdat switch niet met doubles werkt
		    int pc = (int) (2*probCross);
			    	
			// perform operators
		    System.out.println("PC = "+pc+" and popSize="+popSize);
		    switch(pc){
		 		case 0:
		 	 		for (int solId=0; solId < popSize; solId++){
		 			    // mutation
		 	 			// compute fitness and add to childpool
		 	 			Solution child = mutation((Solution)parentPool.get(solId));
	 	   				childPool.add(child); 
		 	 		}
		 	 		break;
		 	   	case 1:
		 	   		for (int solId=0; solId<popSize-1; solId+=2){
		 	   			if (bitGenerator.nextFloat() <0.5){
		 	   				// do crossover
		 	   				ArrayList children = crossOver((Solution)parentPool.get(solId), (Solution)parentPool.get(solId+1));
		 	   				childPool.addAll(children);
		 	   				
		 	   			} else{
		 	   				// do mutation
		 	   				Solution child1 = mutation((Solution)parentPool.get(solId));
		 	   				Solution child2 = mutation((Solution)parentPool.get(solId+1));
		 	   				childPool.add(child1); 
		 	   				childPool.add(child2);
		 	   			}
		 	   		}
		 	   		break;
		 	   	case 2:
		 	   			for (int solId=0; solId<popSize-1; solId+=2){
		 	   				// crossover
		 	   				// compute fitness and add to childpool population.set(solId,mutation((Solution)population.get(solId)));
		 	   				ArrayList children = crossOver((Solution)parentPool.get(solId), (Solution)parentPool.get(solId+1));
		 	   				childPool.addAll(children);
		 	   			}
		 	   	default:
		 	   		break;
		    }
		    System.out.println();
		    System.out.println("I get here?");
		    
		    System.out.println("Sorting childpool, then cutting it in half");
		    childPool = quickSort(childPool);
		    System.out.println();
		    System.out.println(childPool.toString());
		    childPool.subList(popSize,childPool.size()).clear();
		    System.out.println(childPool.toString());
		    System.out.println("childPool?");
		    if (((Solution) childPool.get(popSize-1)).getId()== 
		    	   ((Solution)parentPool.get(popSize-1)).getId()){
		    	unchanged+=1;
		    } else {
		    	unchanged =0;
		    }
			// sort childpool
			// update unchanged
			// add childpool to population
			// take the (popSize) best
			//runGa(nrOfGen+1);
		} 
		else {
			return nrOfGen;
		}
		return nrOfGen;
	}
	

}

