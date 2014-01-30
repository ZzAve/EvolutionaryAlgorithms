import java.util.ArrayList;

/**
 * Class Node is the class that entails all information about the graph (U graph or
 * G graph). It keeps a static list of all instantiated Nodes for easy enumeration.
 * 
 * It copies the information from the 'U500.05.txt' (or similar) directly, assuming it is 
 * whitespace separated.
 * @author Myrna, Julius
 *
 */
public class Node {

	private int id;
	private static int totalId=0;
	private static Node[] allNodes = new Node[501];
	private int amountEdges;
	private ArrayList<Integer> neighbours;
	private float coordX;
	private float coordY;

	/**
	 * Constructor Node constructs a node. It requires an inputstring which is a whitespace 
	 * separated file that contains the following items (in that order)
	 * 1 id
	 * 2 coordinates
	 * 3 # of neighbours
	 * 4 until ..  neighbours IDs
	 **/
	public Node(String nodeProp, int graph_type) {
		
		//Split the incoming string, based on the whitespace.
		String[] props2 = nodeProp.split(" ");
		ArrayList<String> props= new ArrayList<String>();
		for (int j=0; j<props2.length;j++){
			if (!props2[j].equals(""))
				props.add(props2[j]);
		}

		//Show the input (nicely ordered)
		//for (int j=0;j<props.size();j++)
		//	System.out.print(props.get(j).toString()+", ");
		//System.out.println();
			
		
		//Get Id
		id = Integer.parseInt(props.get(0));
		
		//Get node axes
		String[] coords = props.get(1).split(",");
		coordX = Math.round(100*Float.parseFloat( coords[0].substring(1) ));
		coordY = Math.round(100*Float.parseFloat( coords[1].substring(0,coords[1].length()-1 ) ));
		
		//Read amount of edges and neighbours
		amountEdges = Integer.parseInt( props.get(2) );
		
		//Read all the neighbours
		neighbours = new ArrayList<Integer>();
		for (int i=0;i<amountEdges;i++){
			neighbours.add( Integer.parseInt( props.get(3+i) ) );
		}
		
		allNodes[id] = this;
		totalId++;
		
	}
	
	public int getId(){
		return id;
	}
	public static Node getNode(int idee){
		return allNodes[idee];
	}
	public ArrayList<Integer> getNeighbours(){
		return neighbours;
	}
	
	/**
	 * getCoordinates gives the coordinates of the Node with the id 'idee'
	 * @param idee the id of the Node the coordinates are requested from
	 * @return the coordinates of the Node with id 'idee'
	 */
	public static float[] getCoordinates(int idee){
		return new float[]{allNodes[idee].coordX,allNodes[idee].coordY};
	}
	
	public int getNeighbourSize(){
		return amountEdges;
	}
	
	public static ArrayList<Integer> getNeighbours(int idee){
		return allNodes[idee].getNeighbours();
	}

	/**
	 * distance calculates the Euclidean distance between two Nodes
	 * @param firstNode the first Node
	 * @param secondNode the second Node
	 * @return the Euclidean distance between the two nodes, multiplied with a 
	 * factor of 100.000
	 */
	public static long distance(int firstNode, int secondNode) {
		float[] fst = getCoordinates(firstNode);
		float[] snd = getCoordinates(secondNode);
		
		double diff = Math.abs(fst[0]-snd[0])+Math.abs(fst[1]-snd[1]);
		//System.out.println("diff:  "+diff);
		return (long)diff;
	}
	
}

