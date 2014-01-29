import java.util.Arrays;

/**
 * Statistics is a small helper class in which some statistics
 * and probabilities are computed for a given set of answers to 
 * the local search functions.
 * @author Myrna, Julius
 *
 */
public class Statistics {

	/**
	 * Best determines the best result present in the list of results
	 * @param results the obtained results
	 * @return the Id and cutsize of the best solution found
	 */
	public static long[] best(Answer[] results){
		long best = Long.MAX_VALUE; int bestId=-1;
		for (int i=0;i<results.length;i++){
			if (results[i].solution.getCutsize()<best){
				best = results[i].solution.getCutsize();
				bestId = i;
			}
			
			
		}
		return new long[] {bestId,best};
	}
	
	/**
	 * avgSwap calculated the average number of swaps performed before
	 * the solution was returned.
	 * @param results the obtained results
	 * @return the average number of swaps performed per run
	 */
	public static int avgSwap(Answer[] results){
		int total=0;
		for (int i=0;i<results.length;i++){
			total+= results[i].nrOfSwaps;
		}
		return (total/results.length);
	}
	/**
	 * avgTime calculates the average time that was needed to perform
	 * one run.
	 * @param results the obtained results
	 * @return the average time per run in ms
	 */
	public static int avgTime(Answer[] results){
		int total=0;
		for (int i=0;i<results.length;i++){
			total+= (results[i].time/1000000.0);
		}
		return (total/results.length);
	}
	
	/**
	 * Median gives the median of the results (cutsize)
	 * @param results the obtained results
	 * @return the median of the cutsizes for all the runs
	 */
	public static double median(Answer[] results) {
		int numRuns = results.length;
		long[] optima = new long[numRuns];
		// first sort the stuff
		for (int i=0;i<results.length;i++){
			optima[i] = results[i].solution.getCutsize();
		}
		
		Arrays.sort(optima);

		double median = -1;
		
		if(numRuns % 2 == 0) {
			double loc1 = (double) optima[(numRuns /2) -1];
			double loc2 = (double) optima[(numRuns /2)];
			median = (loc1 + loc2) / 2;
		}
		else {
			median = (double) optima[(int) Math.round((numRuns /2) - 0.5)];
		}
	
		return median;
	}
	
	/**
	 * mean calculates the mean cutsize for all runs
	 * @param results the obtained results
	 * @return the mean cutsize for all runs
	 */
	public static double mean(Answer[] results) {
		int numRuns = results.length;
		int total = 0;
		for (int i=0;i<results.length;i++){
			total += results[i].solution.getCutsize();
		}
		
		return ((double)total) / numRuns;
		
	}
	
	/**
	 * Variance calculated the variance that was present in the obtained results, 
	 * assuming a Gaussian (normal) distribution
	 * @param results the obtained results
	 * @return the (Gaussian) variance of the obtained results
	 */
	public static double variance(Answer[] results) {
		int numRuns = results.length;
		long[] optima = new long[numRuns];
		// first sort the stuff
		for (int i=0;i<results.length;i++){
			optima[i] = results[i].solution.getCutsize();
		}
		double mean = mean(results);
		
		double variance = 0;
		double sum = 0;
		
		for(int i=0; i < numRuns; i++) {
			double d = optima[i] - mean;
			sum += (d*d);
	
		}
		
		variance = sum / numRuns;
		
		return variance;
	}
	
	/**
	 * Ttest performs a ttest on two different performances (e.g. MLS vs ILS)
	 * @param mean1
	 * @param mean2
	 * @param var1
	 * @param var2
	 * @param runs1
	 * @param runs2
	 * @return
	 */
	public static double tTest(double mean1, double mean2, double var1, double var2, int runs1, int runs2) {		
		double diff = Math.abs(mean1 - mean2);
		double d = (var1 / runs1) + (var2 / runs2);
		return diff / (Math.sqrt(d));
	}
	
	
}
