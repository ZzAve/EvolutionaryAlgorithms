import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Ga{
	
	private int fitnessFunction;
	private ArrayList population;
	private int popSize;
	private int tourSize;
	
	public Ga(int fitnessFunctionType,int populationSize, int tournamentSize){
		
		fitnessFunction = fitnessFunctionType;
		popSize = populationSize;
		tourSize = tournamentSize;
		
		runGa(50,new String());
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
		return winners;
	}
	
	private void crossOver(Solution sol1,Solution sol2){
		// what strategy?
	}
	
	private void mutation(Solution sol){
		Random randomNr = new Random();
		float mutationGuess = randomNr.nextFloat();
		int nrOfMutations=0;
		while (mutationGuess < 1 / (2^(nrOfMutations+1) ) ){
			nrOfMutations++;
		}
		
		//Pick  random numbers!
		int[] mutations = new int[nrOfMutations];
		int nb_picked =0;
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
		sol.mutateSolution(mutations);
	}
		
		// perform nrOfMutations number of mutations!
	
	public String runGa(int nrOfTimes,String answer){
		if (nrOfTimes > 0) {
			
			/* For a parameter setting
			 *   instantiate a random population (with x solutions)
			 */
		    int[] dummySolution = new int[100];
			Random bitGenerator = new Random();
			for (int popNr=0;popNr<popSize;popNr++){
				for (int solNr=0; solNr<100;solNr++ ){
					dummySolution[solNr] = bitGenerator.nextInt(2);
				}
				population.add(new Solution(dummySolution,fitnessFunction));	
			}
			 /*
			 *   get maxFitness for the current case*/
		     int T = ((Solution) population.get(10)).getMaxFitness(fitnessFunction);
		     
		     while(T<Integer.MAX_VALUE){
		    	 performTournament(tourSize);
		    	 //crossOver();
		    	 //mutation();
		    	 //creation of children pool
		     }
		     
			 /*   do while unchanged < 10*popsize or T=INTEGER.MAX_VALUE
			 *     selection through tournament of size s
			 *     apply crossover
			 *     apply mutation
			 *     select childpool (best popsize) (also update 'unchanged' and T)
			 * 
			 *   Repeate the above 50 times!
			 */
		     
		     int runnr= 50 - nrOfTimes;
		     answer= answer.concat("Run nr 50 - Succes!");
		     return runGa(nrOfTimes-1,answer);
		} else {
			return answer;
		}
	}
}
