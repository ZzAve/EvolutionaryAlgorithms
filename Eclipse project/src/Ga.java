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
		
		/*for(int size=0; size<popSize; size++){
			
			int[] solution = new int[100];
			
			for(int bit=0; bit<100; bit++){	
				solution[bit] = (int) Math.random();
			}
			
			Solution sol = new Solution(solution, fitnessFunction);
			population.add(sol);
		}*/
		
		//runGa(50,new String());
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
	
	private Solution mutation(Solution sol){
		Random randomNr = new Random();
		int nrOfMutations=0;
		while (randomNr.nextFloat()<0.5 ){
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
		return sol;
		
	}
		
	public String runGa(String answer){
		return runGa(100,answer);
	}
	
	private String runGa(int nrOfTimes,String answer){
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
		     int T = Solution.getMaxFitness(fitnessFunction);
		     
		     while(T==T){ //+ unchanged condition for 10*popSize runs
		    	 ArrayList parentPool = performTournament(tourSize);
		    	 for (int solId=0;solId<popSize;solId+=2){
		    		 if (bitGenerator.nextFloat() <0.5){
			    		 //do crossover
		    			 
			    	 } else {
			    		 //do mutation
			    		 //population.set(solId,mutation((Solution)population.get(solId)));
			    		 
			    	 }
		    		 
		    		 
		    		 
		    	 }
		    	 
		    	 
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
