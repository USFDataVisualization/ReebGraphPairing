package src.junyi.reebgraph;
//package usf.saav.topology.join;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import src.junyi.reebgraph.cmd.ReebGraph;


public class ReebGraphLoader extends ReebGraph {

	private static final long serialVersionUID = 7889260039234787058L;
	
	///private int noNodes=0;
	//private int noArcs=0;

	String inputReebGraph;



	
	public ReebGraphLoader(String _inputReebGraph) throws IOException {
		
		inputReebGraph = _inputReebGraph;

		HashMap<Integer, ReebGraphVertex> rvmap = new HashMap<Integer, ReebGraphVertex>();
		BufferedReader reader;

			reader = new BufferedReader(new FileReader(inputReebGraph));
			String s = reader.readLine();

			String[] r = s.split("\\s");

			while(s != null) {
				r = s.split("\\s");
				if (r[0].trim().equals("v") == true) {			     

					//noNodes++;
					// System.out.println(s);
					int v;
					float  fn;

					v = Integer.parseInt(r[1].trim());

					fn = Float.parseFloat(r[2].trim());


					ReebGraphVertex reebV= createVertex(fn, v);

					//System.out.println(v + " <==> " + reebV.id() );

					rvmap.put(v, reebV);

					//rv.add(reebV);

					s = reader.readLine();

				} 
				if (r[0].trim().equals("e") == true) {
					//noArcs ++;

					int v1 = -1;
					int v2 = -1;

					//	System.out.println(s);

					if(r.length == 3) {
						v1 = Integer.parseInt(r[1]);
						v2 = Integer.parseInt(r[2]);

					} else {
						System.err.println("Invalid input");
						System.exit(0);
					}

					rvmap.get(v1).addNeighbor(rvmap.get(v2));
					rvmap.get(v2).addNeighbor(rvmap.get(v1));

					//	 System.out.println("neighbor# = " + rvmap.get(v1).neighbors().length); 
					//     System.out.println("neighbor# = " + rvmap.get(v2).neighbors().length);
					s = reader.readLine();
				}
			}
			
			reader.close();


//			System.out.println("Vertex =+++++++++++++++++++ "); 
			// printVertices(rv);

//			new ReebGraphPairing( reebMesh );
			//new ReebGraphPairingMerging( reebMesh );

//			System.out.println("No. of Nodes : " + noNodes);
//			System.out.println("No. of Arcs : " + noArcs);



	}

}