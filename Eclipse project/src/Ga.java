import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


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
		
		System.out.println("Fitnesses: ");
		for(int solNr=0;solNr<popSize;solNr++) {
			Solution sol = (Solution) population.get(solNr);
			System.out.println((int) sol.getFitness());
		}
		System.out.println("Fitnesses ");
		
		population = quickSort(population);
		
		System.out.println("Fitnesses: ");
		for(int solNr=0;solNr<popSize;solNr++) {
			Solution sol = (Solution) population.get(solNr);
			System.out.println((int) sol.getFitness());
		}
		System.out.println("Fitnesses ");
		
		Solution best = (Solution) population.get(0);
		bestFitness = (int) best.getFitness();
	}
	
	private ArrayList performTournament(int tournamentSize){
		ArrayList winners = new ArrayList();
		switch (tournamentSize){
		  case 1:
			  Random randomNr = new Random();
			  for (int poolEntry=0;poolEntry<popSize;poolEntry++){
				 winners.add(population.get(randomNr.nextInt(population.size())));
			  }
			  break;
		  case 2:
			  ArrayList tournPop = population;
			  for (int round=1;round<3;round++){
				System.out.println("The population before shuffling:");
				System.out.println(tournPop.toString());
				Collections.shuffle(tournPop);
				System.out.println("The population after shuffling:");
				System.out.println(tournPop.toString());
				
				for (int fighter1=0; fighter1<popSize-1; fighter1+=2){
					if (((Solution)tournPop.get(fighter1)).getFitness() <= 
						   ((Solution)tournPop.get(fighter1+1)).getFitness()){
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

		System.out.println("Tour fitnesses: ");
		
		for(int i=0; i<winners.size(); i++) {
			System.out.println(((Solution)winners.get(i)).getFitness());
		}
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
		
		children.add(new Solution(child1, fitnessFunction, linkage));
		children.add(new Solution(child2, fitnessFunction, linkage));
		return children;
	}
	
	private Solution mutation(Solution sol){
		Solution mutatedSol = new Solution(sol.getSolution(),fitnessFunction,linkage);
		Random randomNr = new Random();
		int nrOfMutations=0;
		while (randomNr.nextFloat()<0.5 ){
			nrOfMutations++;
		}
		
		//Pick  random numbers!
		int[] mutations = new int[nrOfMutations];
		int nb_picked = 0;
		int index;
		while (nb_picked < nrOfMutations){
		   index = randomNr.nextInt(100);
		   boolean found=false;
		   for(int i=0;i<nb_picked;i++){
			    if(mutations[i]==index){
			    	found=true;
			    	break;
			    }
		   }
			  if(!found)
				  mutations[nb_picked]=index;
		} 
		mutatedSol.mutateSolution(mutations);
		return mutatedSol;
		
	}
		
	public ArrayList runGa(){
		
		// result consists of number of optimum found, number of generations (and maybe highest fitness)
		int numOpt = 0;
		int numGen = 0;
		ArrayList result = new ArrayList();
		
		for(int run=0; run<1; run++){
			generatePopulation();
			unchanged=0;
			
			int gen = runGa(0);
			// update numOpt and numGen
			Solution.resetIds();
			
		}
		//result.add(numOpt);
		//result.add(numGen);

		return result;
	}
	
	private int runGa(int nrOfGen){
		//+ unchanged condition for 10*popSize runs
		if ((unchanged < nrOfGenerations) && (bestFitness < maxFitness)) {
			ArrayList parentPool = performTournament(tourSize);
		    ArrayList childPool = new ArrayList();
		    childPool.addAll(parentPool);
		    // puur omdat switch niet met doubles werkt
		    int pc = (int) (2*probCross);
			    	
			// perform operators
		    switch(pc){
		 		case 0:
		 	 		for (int solId=0; solId<popSize; solId+=2){
		 			    // crossover
		 	 			// compute fitness and add to childpool
		 	 		}
		 	 		break;
		 	   	case 1:
		 	   		if (bitGenerator.nextFloat() <0.5){
		 	   			for (int solId=0; solId<popSize; solId+=2){
		 	   				ArrayList children = crossOver((Solution)parentPool.get(solId), (Solution)parentPool.get(solId+1));
		 	   				childPool.addAll(children);
		 	   				
		 	   			}
		 	   		}
		 	   		else {
		 	   			for (int solId=0; solId<popSize; solId++){
		 	   				// mutation
		 	   				// compute fitness and add to childpool 
		 	   				// population.set(solId,mutation((Solution)population.get(solId))); 
		 	   			}
		 	   		} 
		 	   		break;
		 	   	case 2:
		 	   			for (int solId=0; solId<popSize; solId++){
		 	   				// mutation
		 	   				// compute fitness and add to childpool population.set(solId,mutation((Solution)population.get(solId))); 
		 	   			}
		 	   	default:
		 	   		break;
		    }	 
		    
		    
		    childPool = quickSort(childPool);
		    childPool.subList(0,popSize-1);
		    
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
							System.out.println("R.size = " + L.size());
						}
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
