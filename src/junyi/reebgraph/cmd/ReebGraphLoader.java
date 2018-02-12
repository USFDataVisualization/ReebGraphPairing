package junyi.reebgraph.cmd;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import junyi.reebgraph.ReebGraph;


public class ReebGraphLoader extends ReebGraph {

	private static final long serialVersionUID = 7889260039234787058L;

	String inputReebGraph;

	public ReebGraphLoader(String _inputReebGraph) throws Exception {

		inputReebGraph = _inputReebGraph;

		HashMap<Integer, ReebGraphVertex> rvmap = new HashMap<Integer, ReebGraphVertex>();
		BufferedReader reader;

		reader = new BufferedReader(new FileReader(inputReebGraph));
		String s;

		while( (s = reader.readLine()) != null) {
			String[] r = s.split("\\s");
			if (r[0].trim().equals("v") == true) {			     

				if(r.length != 3) {
					reader.close();
					throw new Exception("Invalid edge input");
				}
				
				int    v = Integer.parseInt(r[1].trim());
				float  fn = Float.parseFloat(r[2].trim());
				
				rvmap.put( v, createVertex(fn, v) );

			} 
			if (r[0].trim().equals("e") == true) {

				if(r.length != 3) {
					reader.close();
					throw new Exception("Invalid edge input");
				}
				
				ReebGraphVertex v1 = rvmap.get(Integer.parseInt(r[1]));
				ReebGraphVertex v2 = rvmap.get(Integer.parseInt(r[2]));
				
				v1.addNeighbor(v2);
				v2.addNeighbor(v1);

			}
		}

		reader.close();

	}

}