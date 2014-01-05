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
        private ArrayList result;
        private int solLength;
        private int popSize;
        private int tourSize;
        private int fitnessFunction;
        private int linkage;                // 1=tight, 2=random
        private double probCross;           // probability of crossover (else mutation) 
        private int crossType;              // 1=2point, 2=uniform
        private int nrOfGenerations;        // at most 10*popSize 
        private int bestFitness;            // current best fitness of population
        private int maxFitness;             // highest fitness value possible (given fitness function)
        Random bitGenerator;
        int unchanged;                      // keeps track of change in population
        
        /**
         * Constructor Ga 
         * @param solutionLength
         * @param populationSize
         * @param tournamentSize
         * @param fitnessFunctionType
         * @param linkageType
         * @param probCrossover
         * @param crossoverType
         */
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
        
        public ArrayList getResult(){
        	return result;
        }
        
        /**
         * 
         */
        private void generatePopulation(){
        	population = new ArrayList();
            // instantiate a random population (with x solutions)
            for (int solNr=0;solNr<popSize;solNr++){
            	int[] dummySolution = new int[solLength];
                for (int bitNr=0; bitNr<solLength;bitNr++ ){
                        dummySolution[bitNr] = bitGenerator.nextInt(2);
                }
                
                Solution sol = new Solution(dummySolution,fitnessFunction,linkage);
                population.add(sol);
                
                //clear memory
                sol = null;
                dummySolution = null;
            }
            
            //System.out.println();
            //System.out.print("Fitnesses before: ");
            for(int solNr=0;solNr<popSize;solNr++) {
                    Solution sol = (Solution) population.get(solNr);
                    //System.out.print(sol.getFitness()+", ");
            }
            
            //System.out.println();
            //System.out.print("Sorting: ");
            population = quickSort(population);
            //System.out.println();
            //System.out.print("Fitnesses after: ");
            for(int solNr=0;solNr<popSize;solNr++) {
                    Solution sol = (Solution) population.get(solNr);
                    //System.out.print(sol.getFitness()+", ");
            }
            //System.out.println();
            Solution best = (Solution) population.get(0);
            bestFitness = (int) best.getFitness();
            
            //System.out.println();
        }
        
        
        /**
         * 
         * @param tournamentSize
         * @return
         */
        private ArrayList performTournament2(int tournamentSize){
        		ArrayList winners = new ArrayList();
                Random randomNr = new Random();

                switch (tournamentSize){
                case 1: 
	                //random
	                for (int poolEntry=0;poolEntry<2;poolEntry++){
                        winners.add(population.get(randomNr.nextInt(popSize)));
	                }
	                break;
                case 2:
                    // let two units fight
                    for (int i=0;i<2;i++){
                        int sol1, sol2;
                        sol1 = randomNr.nextInt(popSize); 
                        sol2 = randomNr.nextInt(popSize);
        
                        while (sol1 == sol2){
                            sol2 = randomNr.nextInt(popSize);
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
                //System.out.println("Size of winners (tournament: "+winners.size());
                return winners;
        }

        /**
         * 
         * @param parent1
         * @param parent2
         * @return
         */
        private ArrayList crossOver(Solution parent1,Solution parent2){
            int[] child1 = new int[solLength];
            int[] child2 = new int[solLength];
            ArrayList children = new ArrayList();
            
            switch(crossType){
            case 2: //uniform crossover
	            for(int bit=0; bit<solLength; bit++) {
                    int rand = bitGenerator.nextInt(2);
                    if(rand==0) {
                        child1[bit] = parent1.getBitString()[bit];
                        child2[bit] = parent2.getBitString()[bit];
                    } else {
                        child1[bit] = parent2.getBitString()[bit];
                        child2[bit] = parent1.getBitString()[bit];
                    }
	            }
	            break;
            case 1: //2point crossover
            	int cross1,cross2;
            	if(fitnessFunction ==3 || fitnessFunction ==4){
            		cross1 = bitGenerator.nextInt(solLength/4);
            		cross2 = bitGenerator.nextInt(solLength/4);
            		while (cross1==cross2) cross2 = bitGenerator.nextInt(solLength/4);
            		cross1*=4; cross2*=4;
            	} else {
	            	cross1 = bitGenerator.nextInt(solLength); //left border
	                cross2 = bitGenerator.nextInt(solLength); //right border
	                while(cross1==cross2) cross2 = bitGenerator.nextInt(solLength);
            	}
            	
                if(cross1>cross2) { // ensure left border has a lower index than the right border
                    int temp = cross1;
                    cross1 = cross2;
                    cross2 = temp;
                }
                
                for(int bit=0; bit<solLength; bit++) {
                    if(bit<cross1 || bit>=cross2) {
                    	// outside crossover area
                        child1[bit] = parent1.getBitString()[bit];
                        child2[bit] = parent2.getBitString()[bit];
                    }
                    else {
                    	// inside crossover area
                        child1[bit] = parent2.getBitString()[bit];
                        child2[bit] = parent1.getBitString()[bit];
                    }
                }
                break;
            default: break;
            }
	            
            // Create two new solutions, whose bitstring is given by the just calculated one
            children.add(new Solution((int[])child1.clone(), fitnessFunction, linkage));
            children.add(new Solution((int[])child2.clone(), fitnessFunction, linkage));
            
    
            //System.out.print(" Crossover: p1:"+parent1.getFitness()+" p2:"+parent2.getFitness());
            //System.out.println(" Child1:"+((Solution) children.get(0)).getFitness()+" child2:"+((Solution) children.get(1)).getFitness());
            return children;
        }
        
        /**
         * 
         * @param sol
         * @return
         */
        private Solution mutation(Solution sol){
            Random randomNr = new Random();
            Solution mutatedSol = new Solution((int[])sol.getBitString().clone(),sol.getType(),sol.getLinkage());
            
            int nrOfMutations = 0;
            while (randomNr.nextFloat() <0.5 ){
                nrOfMutations++;
            }
            
            if (nrOfMutations!=0){
                //Pick  random numbers!
                int[] mutations = new int[nrOfMutations];
                mutations[0]=randomNr.nextInt(solLength);
                        
                //initialize
                int nb_picked = 0;
                int index;
                boolean found = false;
                
                while (nb_picked < nrOfMutations){
                   index = randomNr.nextInt(solLength);
                   found = false;
                   for(int i=0;i<nb_picked;i++){
                      if(mutations[i]==index){
                         found = true;
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
            //System.out.println("Non-mutated solution: "+ sol.bitStringString());
            //System.out.println("Mutated solution: " + mutatedSol.bitStringString());
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
         * 
         * @param sol
         */
        private void insert(Solution sol){
            int mid = population.size()/2;
            if (sol.getFitness() >= ((Solution) population.get(mid)).getFitness()){
                // add in left halve
                insert(sol,0,mid);
            } else {
                insert(sol,mid,population.size());
            }
        }
           
        /**
         * 
         * @param sol
         * @param left
         * @param right
         */
        private void insert(Solution sol, int left, int right){
            if (right - left >= 2){                        
                int mid = (right + left)/2;
                if (sol.getFitness() > ((Solution)population.get(mid)).getFitness()){
                    insert(sol,left,mid);
                } else {
                    insert(sol,mid,right);
                }
            } else {
            	//System.out.println("Old pop: "+population.toString());
                if (sol.getFitness() >= ((Solution) population.get(left)).getFitness()){
                    population.add(left, sol);
                } else {
                    population.add(right,sol);
                    
                }

                population.remove(population.size()-1);
                //System.out.println("New pop: "+population.toString());
                //System.out.println("Change at: "+left+" - "+right);
                
            }
        }
        
        /**
         * 
         * @return
         */
        public ArrayList runGa(){
    		ArrayList result = new ArrayList();
    		//System.out.println("Start!");
            return runGaa(0,result);
        }
        
        /**
         * 
         * @param trialNr
         * @param answer
         * @return
         */
        private ArrayList runGaa(int trialNr, ArrayList answer){
            ArrayList winners;
            if (trialNr<50){
            	//System.out.println();
            	//System.out.println(">> Trial: "+trialNr);
                // set up
                Solution.resetIds();
                population =null;
                generatePopulation();
                unchanged=0; 
                bestFitness=((Solution) population.get(0)).getFitness();
                int evaluations=0;
                
                // run until...
                while ((unchanged < nrOfGenerations) && (bestFitness < maxFitness) && (evaluations<1000000)){
                	/*if (evaluations%100000==0){
                		System.out.print(evaluations+", ");
                	}*/
                	// select
                    winners = performTournament2(tourSize);
                    //System.out.println("Winners of tournament: "+winners.toString());
                    
                    // crossover or mutate
                    if (probCross == 0){
                    	  // mutation
                          // compute fitness and add to childPool
                          for (int i=0; i<winners.size();i++) {
                              winners.set(i, mutation((Solution) winners.get(i)));
                       	  }
                    } else if (probCross == 0.5){ 
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
                         //System.out.println("Replacing solution: "+((Solution) population.get(population.size()-1)).getFitness());
                         //System.out.println("Size of pop: "+population.size());
                    	 //System.out.println("BestSol: "+bestSol.bitStringString());
                    	 insert(bestSol);
                         unchanged=0;
                     } else{
                         unchanged++;
                     }
                     
                     // add 1 to counter
                     evaluations++;
                     bestFitness = ((Solution) population.get(0)).getFitness();
                     
                } // end while loop
                
                //add info to answer arraylist
                int[] results  = {bestFitness==maxFitness?1:0,evaluations,bestFitness};
                answer.add(results);
                
                return runGaa(trialNr+1, answer);
            } else {
            	result = answer;
                return answer;
        }
        }
        
        /**
         * 
         * @param solutions
         * @return
         */
        public int averageFitness(ArrayList solutions) {

                int sumFitness = 0;
                
                for(int i=0; i<solutions.size(); i++) {
                        sumFitness += ((Solution)solutions.get(i)).getFitness();
                }
                                        
                return (int) (sumFitness/solutions.size());
        }

}
