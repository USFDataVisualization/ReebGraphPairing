package src.junyi.reebgraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;


import src.junyi.reebgraph.loader.MeshLoader;

//import src.junyi.reebgraph;


public class ReebLoader2 implements MeshLoader{

    private BufferedReader reader;
	private int noNodes=0;
	private int noArcs=0;
	private int curNode;
	private int curArc;
	String inputReebGraph;
	//private ReebGraph rg=new ReebGraph();
	
public void setInputFile(String _inputReebGraph) {
	inputReebGraph = _inputReebGraph;
		try {
			reader = new BufferedReader(new FileReader(inputReebGraph));
			String s = reader.readLine();
			
			String[] r = s.split("\\s");
			
			while(s != null) {
				r = s.split("\\s");
			  if (r[0].trim().equals("v") == true) {			     
				  
				   noNodes++;
				   s = reader.readLine();
				} 
			  else {
				    noArcs ++;
					s = reader.readLine();
			       }
			}  

			System.out.println("No. of Nodes : " + noNodes);
			System.out.println("No. of Arcs : " + noArcs);

			curNode = 0;
			curArc = 0;
			
			reader = new BufferedReader(new FileReader(inputReebGraph));

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}


public int getRowCount() {
	return noNodes+noArcs;
}


public Simplex getNextSimplex() {
		try {
			
			String s = reader.readLine();
			String[] r = s.split("\\s");
			
			
			if ( r[0].equals("v") ) {
				int v;
				float  fn;
				//String s = reader.readLine();
				//String[] r = s.split("\\s");
				v = Integer.parseInt(r[1].trim());
				
				fn = Float.parseFloat(r[2].trim());
				
				
				Node node = new Node();
				
				node.v = v;
				
				node.fn = fn;

				curNode++;
				return node;
			}
			if ( r[0].equals("e") ) {
				//String s = reader.readLine();
				//System.out.println(s);
				//String[] r = s.split("\\s");
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
				

				curArc++;
				return arc;
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}




public void reset() {
	// TODO Auto-generated method stub
	
}
	




}