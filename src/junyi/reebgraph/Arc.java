package src.junyi.reebgraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class Arc implements  Serializable {
	private static final long serialVersionUID = 1L;

	int v1,v2;
	Node n1;
	Node n2;
	//float fn;
	
	boolean removed;
	//ArrayList <Integer> comps = new ArrayList<Integer>();
	
	
	ArrayList<Node> path = new ArrayList<Node>();
	HashSet<Node> segment = new HashSet<Node>();
	
	

	//public int compareTo(Arc o) {
	//	float ff = fn - o.fn;
	//	if(ff < 0)
	//		return -1;
	//	else if(ff > 0)
	//		return 1;
	//	return 0;
	//}
}
