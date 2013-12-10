import java.util.ArrayList;

/* For a parameter setting
 *   instantiate a random population (with x solutions)
 *   get maxFitness for the current case
 *   do while unchanged < 10*popsize or T=INTEGER.MAX_VALUE
 *     selection through tournament of size s
 *     apply crossover
 *     apply mutation
 *     select childpool (best popsize) (also update 'unchanged' and T)
 * 
 *   Repeat the above 50 times!
 * 
 * Process results and do another parameter setting (or 50 or parameter settings or so)
 */

public class Main{
	
	public static void main(String[] args){
		// Select parameter settings
		
		// We moeten allerlei combinaties van parameter values afgaan, maar heb nog voor het gemak 1 variant.
		int solutionLength = 100;
		int populationSize = 100;		// multiple of 10
		int tournamentSize = 1;			// 1 or 2
		int fitnessFunctionType = 1;	// 1,2,3,4
		int linkageType = 1;			// 1=tight, 2=random
		double probCrossover = 0;		// 0, 0.5, 1
		int crossoverType = 1;			// 1=2point, 2=uniform
		
		// Perform GA
			
		Ga ga = new Ga(solutionLength, populationSize, tournamentSize, fitnessFunctionType, linkageType, probCrossover, crossoverType);
		//	
		// number of generations after the global optimum was found
		ArrayList result = ga.runGa();
			
			// print result
		
		
	}

}
