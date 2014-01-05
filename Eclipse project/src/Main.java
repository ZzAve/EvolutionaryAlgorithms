import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
		int solutionLength = Solution.solLength;
		int populationSize = 1000;		// multiple of 10   range 200 - 1000
		int tournamentSize = 1;			// 1 or 2
		int fitnessFunctionType = 4;	// 1,2,3,4
		int linkageType = 1;			// 1=tight, 2=random
		double probCrossover = 1;		// 0, 0.5, 1
		int crossoverType = 1;			// 1=2point, 2=uniform
		
		Ga Ga;
		// Perform GA
		for (int fitFunc=1;fitFunc<=2;fitFunc++){
			for(int popsize=100;popsize<=250;popsize+=10){
				for(int toursize=1;toursize<=2;toursize++){
					for (double probCross=0;probCross<=1;probCross+=0.5){
						if (probCross!=0){
								for (int crossType=1;crossType<=2;crossType++){
									
								}
						} else {
							//...
						}
					}
					
				}
			}
		}
		
		ArrayList result1 = new ArrayList();
		ArrayList result2 = new ArrayList();
		for (int fitFunc=3;fitFunc<=4;fitFunc++){
			for(int popsize=200;popsize<=250;popsize+=10){
				for(int toursize=1;toursize<=2;toursize++){
					for (double probCross=0;probCross<=1;probCross+=0.5){
						if (probCross!=0){
							for (int crossType=1;crossType<=2;crossType++){
								if (crossType==1){
									//check linkage
									for (int link=1;link<=2;link++){
										Ga = new Ga(solutionLength,popsize,toursize,fitFunc,link,probCross,crossType);
										double[] settings ={solutionLength,popsize,toursize,fitFunc,link,probCross,crossType};
										result1.add(settings);
										result2.add(Ga.runGa());
									}
								} else {
									//do not check linkage
									Ga = new Ga(solutionLength,popsize,toursize,fitFunc,0,probCross,crossType);
									double[] settings ={solutionLength,popsize,toursize,fitFunc,0,probCross,crossType};
									result1.add(settings);
									result2.add(Ga.runGa());
									
								}
							}
						} else { // probCross ==0
							//...
							Ga = new Ga(solutionLength,popsize,toursize,fitFunc,0,probCross,0);
							double[] settings ={solutionLength,popsize,toursize,fitFunc,0,probCross,0};
							result1.add(settings);
							result2.add(Ga.runGa());
						}
					}
				}
			}
		}
		
		//write to file
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
	    }
	  
		
		
		Ga ga = new Ga(solutionLength, populationSize, tournamentSize, fitnessFunctionType, linkageType, probCrossover, crossoverType);
		//	
		//number of generations after the global optimum was found
		ArrayList result = ga.runGa();
		
		//INFORMATION IN RESULT:
		/* Col1        Col2               Col3
		   WIN/LOSE    #evaluations       avg fitness 
		*/
		// print result
		System.out.println();
		int wins=0;;
		double winratio;
		int[] win = new int[result.size()];
		int[] evaluations = new int[result.size()];
		int[] avgfitness = new int[result.size()];
		for(int i=0;i<result.size();i++){
			wins+=((int[])result.get(i))[0];
			win[i] = ((int[])result.get(i))[0];
			evaluations[i] = ((int[])result.get(i))[1];
			avgfitness[i] =((int[])result.get(i))[2];
		}
		winratio = wins/result.size();
		System.out.println("Results: "+wins);
		System.out.println("Eval       avg fit");
		for (int i=1;i<result.size();i++){
		    System.out.print(evaluations[i]);
		    System.out.println("     "+avgfitness[i]);
		}
		System.out.println("Winratio: "+winratio);
	}

}
