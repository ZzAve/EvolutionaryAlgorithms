import java.util.Collection;
import java.util.Random;

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
	private static int solLength = 100;
	private int functionType;
	private int linkage;
	private static int idTotal=0;
	private int id;
	
	public Solution(int[] bitStringSolution, int type, int link){
		bitString = bitStringSolution;
		solLength = bitString.length;
		functionType = type;
		linkage = link;
		idTotal++;
		id = idTotal;
		setFitness(bitString, functionType, linkage);
		
	}
	
	public int[] getBitString(){
		return bitString;  
	}
	public int getFitness(){
		return fitness;
	}
	public int getLinkage(){
		return linkage;
	}
	public int getType(){
		return functionType;
	}
	
	public static int getMaxFitness(int functionType){
		// Calculate max fitness for all cases! Then use a switch to report it back!
		switch(functionType){
		   case 2:
		   	   return (solLength*(solLength+1))/2;		   	
		   case 1:
		   case 3:
		   case 4:
			   return solLength;
		   default:
			   break;
		}
		return -1;
	}
	
	
	private static int calcFitness(int[] bitString, int functionType, int linkage){
		switch (functionType) {
		case 1:  // uniformly scale counting ones function
			int fit1 =0;
			for (int bit=0;bit<bitString.length;bit++){
				fit1 +=bitString[bit];
			}
			return fit1;
			
		case 2: // Linearly Scaled counting ones function
			int fit2 =0;
			for (int bit=0;bit<bitString.length;bit++){
				fit2 +=bit*bitString[bit];
			}
			return fit2;
			
		case 3: // deceptive trap function k=4, d=1
			if(linkage==2) {
				shuffleArray(bitString);
			}
				
			int fit3=0;
			int[] CO = new int[4];
			int COfit;
			for (int bit=0;bit<bitString.length;bit+=4){
				CO[0] = bitString[bit];  CO[1]=bitString[bit+1];
				CO[2]=bitString[bit+2];  CO[3]=bitString[bit+3];
				COfit = calcFitness(CO,1,linkage);
				if(COfit==4){
					fit3+=4;
				} else {
					fit3+= 3-COfit; //4-1-(4-1)/(4-1)*COfit;
				}
			}
			return fit3;
			
		case 4: // non-deceptive trap function k=4, d=2.5
			if(linkage==2) {
				shuffleArray(bitString);
			}
			
			int fit4=0;
			int[] CO2 = new int[4];
			int CO2fit;
			for (int bit=0;bit<bitString.length;bit+=4){
				CO2[0] = bitString[bit];  CO2[1]=bitString[bit+1];
				CO2[2]=bitString[bit+2];  CO2[3]=bitString[bit+3];
				CO2fit = calcFitness(CO2,1,linkage);
				if(CO2fit==4){
					fit4+=4;
				} else {
					fit4+= 1.5 - 0.5*CO2fit; //4-2.5-(4-2.5)/(4-1)*CO2fit;
				}
			}
			return fit4;
		default:
			break;
		}

		return -1; // if no correct function was set, return -1		
	}
	
	public void setFitness(int[] bitString, int functionType, int linkage){
		fitness = calcFitness(bitString, functionType, linkage);
	}
	
	public int getId(){
		return id;
	}
	
	public static void resetIds(){
		idTotal=0;
		System.out.println("Ids are reset to 0!");
	}
	
	public void mutateSolution(int[] indices){
		for(int j=0; j<solLength; j++) {
			boolean mut = false;
			for (int i=0;i<indices.length;i++){
				if(j==indices[i]) mut = true;
			}
			
			if(mut==true) {
				if (bitString[j]==0){
					bitString[j]=1;
				} else {
				bitString[j]=0;
				}
			}
		}
		setFitness(bitString,functionType,linkage);
	}
	
	
	static void shuffleArray(int[] bits)
	  {
	    Random rand = new Random();
	    for (int i=bits.length-1; i>0; i--)
	    {
	      int index = rand.nextInt(i + 1);
	      // Simple swap
	      int bit = bits[index];
	      bits[index] = bits[i];
	      bits[i] = bit;
	    }
	  }

	public String toString(){
		String answer="Solution<"+getId()+", ";
		
		answer+=getFitness()+">";	
		return answer;
	}
}
