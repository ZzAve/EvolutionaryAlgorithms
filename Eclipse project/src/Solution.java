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
    protected static final int solLength = 100;
    private int functionType;
    private int linkage;
    private int[] linkList;
    private static int idTotal=0;
    private int id;
    
    public Solution(int[] bitStringSolution, int type, int link){
        bitString = bitStringSolution;
        //solLength = bitString.length;
        functionType = type;
        linkage = link;
        idTotal++;
        id = idTotal;
        
        
        // if there is linkage get the combinations.
        if (linkage==2){
            linkList = new int[solLength];
            for (int i = 0; i < solLength; i++) {
              linkList[i]=i;
            }
            shuffleArray(linkList);
        }
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
    
    private int calcFitness(int[] bitString, int functionType, int linkage){
            switch (functionType) {
            case 1: // uniformly scale counting ones function
                    int fit1 =0;
                    for (int bit=0;bit<bitString.length;bit++){
                            fit1 +=bitString[bit];
                    }
                    return fit1;
                    
            case 2: // Linearly Scaled counting ones function
                    int fit2 =0;
                    for (int bit=0;bit<bitString.length;bit++){
                            fit2 +=(bit+1)*bitString[bit];
                    }
                    return fit2;
                    
            case 3: // deceptive trap function k=4, d=1
                            
                    int fit3=0;
                    int[] CO = new int[4];
                    int COfit;
                    for (int bit=0;bit<bitString.length-3;bit+=4){
                    	if(linkage==2){
                    		CO[0] = bitString[linkList[bit]];    CO[1] = bitString[linkList[bit+1]];
                            CO[2] = bitString[linkList[bit+2]];  CO[3] = bitString[linkList[bit+3]];
                    	} else {                    	
                    		CO[0] = bitString[bit];    CO[1] = bitString[bit+1];
                            CO[2] = bitString[bit+2];  CO[3] = bitString[bit+3];
                    	}
                        
                    	COfit = calcFitness(CO,1,linkage);
                        if(COfit==4){
                                fit3+=4;
                        } else {
                                fit3+= 3-COfit; //4-1-(4-1)/(4-1)*COfit;
                        }
                    }
                    return fit3;
                    
            case 4: // non-deceptive trap function k=4, d=2.5
                    int fit4=0;
                    int[] CO2 = new int[4];
                    int CO2fit;
                    for (int bit=0;bit<bitString.length;bit+=4){
                    	if(linkage==2){
                    		CO2[0] = bitString[linkList[bit]];    CO2[1] = bitString[linkList[bit+1]];
                            CO2[2] = bitString[linkList[bit+2]];  CO2[3] = bitString[linkList[bit+3]];
                    	} else {                    	
                    		CO2[0] = bitString[bit];    CO2[1] = bitString[bit+1];
                            CO2[2] = bitString[bit+2];  CO2[3] = bitString[bit+3];
                    	}
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
        for (int i=0;i<indices.length;i++){
        	if (bitString[indices[i]]==0){
                bitString[indices[i]]=1;
            } else {
            	bitString[indices[i]]=0;
            }
        }
        setFitness(bitString,functionType,linkage);
    }
    
    
    static void shuffleArray(int[] bits){
        Random rand = new Random();
        for (int i=bits.length*2; i>0; i--){
            int index1 = rand.nextInt(bits.length);
            int index2 = rand.nextInt(bits.length);
            
            int bit = bits[index1];
            bits[index1] = bits[index2];
            bits[index2] = bit;
        }
    }

    /**
     * bitStringString creates a string representation of the solution, with explicit mention of the bitstring
     * 
     * @return the solution bitstring 'Solution<id,bitstring>'
     */
    public String bitStringString(){
    	String answer="Solution<"+id+",";
    	for (int i=0; i<bitString.length;i++){
    		answer += bitString[i];
    	}
    	return answer+">";
    }
    
    /**
     * GetString creates a string representation of the Solution
     * @retun the solution represented as a string 'Solution<Id,fitness>'
     */
    public String toString(){
            String answer="Solution<"+getId()+", ";
            
            answer+=getFitness()+">";        
            return answer;
    }
}

