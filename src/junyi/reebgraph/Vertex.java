package src.junyi.reebgraph;

import java.util.ArrayList;


public interface Vertex {
	float value();
	ArrayList<Node> neighbors();
	int [] positions();
	int id();
}