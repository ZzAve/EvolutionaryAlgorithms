
/**
 * NOTE: The functions used for evaluating the fitness have an integer 'type' number:
 *   1: Uniformly Scaled Counting Ones Function
 *   2: Linearly Scaled Counting Ones Function
 *   3: Deceptive Trap Function with k=4 and d=1
 *   4: Non-Deceptive Trap Function with k=4 and d=2.5 
 * @author Julius
 */
public class Solution{
	
	private int[] bitString; 
	private int fitness;
	
	public Solution(int[] bitStringSolution,int type){
		bitString = bitStringSolution;
		setFitness(bitString,type);
		
	}
	
	public int[] getSolution(){
		return bitString;
	}
	public int getFitness(){
		return fitness;
	}
	
	public int getMaxFitness(int functionType){
		// Calculate max fitness for all cases! Then use a switch to report it back!
		return 100;
	}
	
	public void setFitness(int[] bitString,int functionType){
		switch (functionType) {
			case 1:  // uniformly scale counting ones function
				break;
			case 2: // Linearly Scaled counting ones function
				break;
			case 3: // deceptive trap function
				break;
			case 4: // non-deceptive trap function
				break;
			default:
				break;
		}
	}
	
}