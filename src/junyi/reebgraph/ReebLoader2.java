package src.junyi.reebgraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

import src.junyi.reebgraph.ReebGraph.Arc;
import src.junyi.reebgraph.ReebGraph.Node;

//import src.junyi.reebgraph;


public class ReebLoader2{

    private BufferedReader reader;
	private int noNodes=0;
	private int noArcs=0;
	private int curNode;
	private int curArc;
	private ReebGraph rg=new ReebGraph();
	
public void setInputFile(String inputReebGraph) {
		try {
			reader = new BufferedReader(new FileReader(inputReebGraph));
			String s = reader.readLine();
			
			String[] r = splitString(s);
			
			while(s != null) {
				r = splitString(s);
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
			//reader = new BufferedReader(new FileReader(inputReebGraph));
			if (curNode < noNodes) {
				int v;
				float  fn;
				String s = reader.readLine();				
				String[] r = splitString(s);
				v = Integer.parseInt(r[1].trim());
				
				fn = Float.parseFloat(r[2].trim());
				
				
				Node node = rg.new Node();
				
				node.v = v;
				
				node.fn = fn;

				curNode++;
				return node;
			}
			if (curArc < noArcs) {
				String s = reader.readLine();
				String[] r = splitString(s);
				int v1 = -1;
				int v2 = -1;
				
				if(r.length == 3) {
					v1 = Integer.parseInt(r[1]);
					v2 = Integer.parseInt(r[2]);
					
				} else {
					System.err.println("Invalid input");
					System.exit(0);
				}

				Arc arc = rg.new Arc();
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


private static String[] splitString(String s) {
		String[] ret = null;
		StringTokenizer tok = new StringTokenizer(s);
		ret = new String[tok.countTokens()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = tok.nextToken();
		}
		return ret;
	}
	




}