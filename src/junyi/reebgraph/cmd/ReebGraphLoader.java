package junyi.reebgraph.cmd;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import usf.saav.topology.reebgraph.ReebGraph;
import usf.saav.topology.reebgraph.ReebGraphVertex;


public class ReebGraphLoader extends ReebGraph {

	private static final long serialVersionUID = 7889260039234787058L;

	
	public static ReebGraph load(String _inputReebGraph) throws Exception {

		String inputReebGraph;
		inputReebGraph = _inputReebGraph;
		
		ReebGraph reeb = new ReebGraph();

		HashMap<Integer, ReebGraphVertex> rvmap = new HashMap<Integer, ReebGraphVertex>();
		BufferedReader reader;

		reader = new BufferedReader(new FileReader(inputReebGraph));
		String s;

		while( (s = reader.readLine()) != null) {
			String[] r = s.split("\\s");
			if( r.length == 0 ) continue;
			if (r[0].trim().equals("v") == true) {			     

				if(r.length != 3) {
					reader.close();
					throw new Exception("Invalid edge input");
				}
				
				int    v = Integer.parseInt(r[1].trim());
				float  fn = Float.parseFloat(r[2].trim());
				
				rvmap.put( v, reeb.createVertex(fn, v) );

			} 
			if (r[0].trim().equals("e") == true) {

				if(r.length != 3) {
					reader.close();
					throw new Exception("Invalid edge input");
				}
				
				ReebGraphVertex v1 = rvmap.get(Integer.parseInt(r[1]));
				ReebGraphVertex v2 = rvmap.get(Integer.parseInt(r[2]));
				
				if( v1 == null || v2 == null ) {
					System.out.println("WARNING: Edge not found " + r[1] + " " + r[2]);
				}
				else {
					v1.addNeighbor(v2);
					v2.addNeighbor(v1);
				}

			}
		}

		reader.close();
		
		return reeb;

	}

}