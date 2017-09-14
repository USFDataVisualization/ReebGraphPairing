package src.junyi.reebgraph;
//package usf.saav.topology.join;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;







import src.junyi.reebgraph.cmd.paulReebMesh;
import src.junyi.reebgraph.cmd.paulReebMesh.ReebVertex;
import src.junyi.reebgraph.loader.MeshLoader;


public class ReebLoader2 implements MeshLoader{

    private BufferedReader reader;
	private int noNodes=0;
	private int noArcs=0;
	
	String inputReebGraph;
	private ReebGraph rg=new ReebGraph();
	
	paulReebMesh rb = new paulReebMesh();
	ArrayList<ReebVertex> rv = new ArrayList<ReebVertex>();
	
	public HashMap<Integer, Node> vmap = new HashMap<Integer, Node>();
	
	public HashMap<Integer, ReebVertex> rvmap = new HashMap<Integer, ReebVertex>();
	
public void setInputFile(String _inputReebGraph) {
	inputReebGraph = _inputReebGraph;
		try {
			reader = new BufferedReader(new FileReader(inputReebGraph));
			String s = reader.readLine();
			
			String[] r = s.split("\\s");
			
			getRg().setupReebGraph();
			
			while(s != null) {
				r = s.split("\\s");
			  if (r[0].trim().equals("v") == true) {			     
				  
				   noNodes++;
				   System.out.println(s);
				   int v;
					float  fn;
					
					v = Integer.parseInt(r[1].trim());
					
					fn = Float.parseFloat(r[2].trim());
					
					
					Node node = new Node();
					
					ReebVertex reebV= rb.createVertex(fn);
					
					node.id = v;
					
					node.fn = fn;
					
					vmap.put(v, node);
					
					rvmap.put(v, reebV);
					
					//rvmap.put(v, node);
					
					getRg().addNode(node.id, node.fn);

					
				   s = reader.readLine();
				   
				} 
			  if (r[0].trim().equals("e") == true) {
				    noArcs ++;
				    
				    int v1 = -1;
					int v2 = -1;
					
					System.out.println(s);
					
					if(r.length == 3) {
						v1 = Integer.parseInt(r[1]);
						v2 = Integer.parseInt(r[2]);
						
					} else {
						System.err.println("Invalid input");
						System.exit(0);
					}

					Arc arc = new Arc();
					arc.v1 = v1;
					arc.v2 = v2;
					
					getRg().vmap.get(v1).addNeighbor(vmap.get(v2));
					getRg().vmap.get(v2).addNeighbor(vmap.get(v1));
					
					getRg().rvmap.get(v1).addNeighbor(rvmap.get(v2));
					getRg().rvmap.get(v2).addNeighbor(rvmap.get(v1));
					
					System.out.println(vmap.get(arc.v1).id());
				    
					s = reader.readLine();
			       }
			}
			

			System.out.println("No. of Nodes : " + noNodes);
			System.out.println("No. of Arcs : " + noArcs);
				
        	 
        	 getRg().printNodes();
        	 
        	 getRg().printVertices();
        	 
        	// getRg().run();
			

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}


public ReebGraph getRg() {
	// TODO Auto-generated method stub
	return rg;
}


public int getRowCount() {
	return noNodes+noArcs;
}


public void reset() {
	// TODO Auto-generated method stub
	
}
	




}