import java.util.ArrayList;

public class Node {

	private int id;
	private static int totalId=0;
	private int amountEdges;
	private ArrayList<Integer> neighbours;
	private float coordX,coordY;
	

	/**
	 * Constructor Node constructs a node. It requires an inputstring which is a whitespace 
	 * separated file that contains the following items (in that order)
	 * 1 id
	 * 2 coordinates
	 * 3 # of neighbours
	 * 4 until ..  neighbours IDs
	 **/
	public Node(String nodeProp) {
		
		//Split the incoming string, based on the whitespace.
		String[] props2 = nodeProp.split(" ");
		ArrayList<String> props= new ArrayList<String>();
		for (int j=0; j<props2.length;j++){
			if (!props2[j].equals(""))
				props.add(props2[j]);
		}

		//Show the input (nicely ordered)
		for (int j=0;j<props.size();j++)
			System.out.print(props.get(j).toString()+", ");
		System.out.println();
			
		
		//Get Id
		id = Integer.parseInt(props.get(0));
		
		//Get node axes
		String[] coords = props.get(1).split(",");
		coordX = Float.parseFloat( coords[0].substring(1) );
		coordY = Float.parseFloat( coords[1].substring(0,coords[1].length()-1 ) );
		
		//Read amount of edges and neighbours
		amountEdges = Integer.parseInt( props.get(2) );
		
		//Read all the neighbours
		neighbours = new ArrayList<Integer>();
		for (int i=0;i<amountEdges;i++){
			neighbours.add( Integer.parseInt( props.get(3+i) ) );
		}
		
		totalId++;
	}
	
	public int getId(){
		return id;
	}
	
	public ArrayList<Integer> getNeighbours(){
		return neighbours;
	}
	
	public float[] getCoordinates(){
		return new float[]{coordX,coordY};
	}
	public int getNeighbourSize(){
		return amountEdges;
	}
}

