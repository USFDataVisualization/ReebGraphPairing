package src.junyi.reebgraph;

import java.util.ArrayList;


public interface Vertex {
	float value();
	ArrayList<Integer> neighbors();
	int [] positions();
	int id();
}