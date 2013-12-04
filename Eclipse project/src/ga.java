import java.util.ArrayList;
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
		switch (tournamentSize){
		  case 1: 
			  break;
		  case 2:
			  break;
		  default:
			  break;
			
		}
		return new ArrayList();
	}
	
	private void crossOver(){
		
	}
	
	private void mutation(){
		
	}
	
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
		    	 crossOver();
		    	 mutation();
		    	 
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