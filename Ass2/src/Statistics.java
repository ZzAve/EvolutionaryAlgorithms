import java.util.Arrays;


public class Statistics {

	public static double median(Answer[] results) {
		int numRuns = results.length;
		int[] optima = new int[numRuns];
		// first sort the stuff
		for (int i=0;i<results.length;i++){
			optima[i] = results[i].solution.getCutsize();
		}
		
		Arrays.sort(optima);
		for(int i=0;i<optima.length;i++){
			System.out.print(optima[i]+",");
		}
		System.out.println();
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
	
	public static double mean(Answer[] results) {
		int numRuns = results.length;
		int[] optima = new int[numRuns];
		// first sort the stuff
		for (int i=0;i<results.length;i++){
			optima[i] = results[i].solution.getCutsize();
		}
		
		int total = 0;
				
		for(int i=0; i < numRuns; i++) {
			total += optima[i];
		}
		
		return ((double)total) / numRuns;
		
	}
	
	public static double variance(Answer[] results) {
		int numRuns = results.length;
		int[] optima = new int[numRuns];
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
	
	public static double tTest(double mean1, double mean2, double var1, double var2, int runs1, int runs2) {

		double t = 0.0;
		
		double diff = Math.abs(mean1 - mean2);
		double d = (var1 / runs1) + (var2 / runs2);
		
		t = diff / (Math.sqrt(d));
		return t;
	}
	
	
}
