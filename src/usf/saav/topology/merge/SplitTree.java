package usf.saav.topology.merge;

import java.util.ArrayList;

import usf.saav.topology.TopoGraph;

public class SplitTree extends MergeTree {
	public SplitTree( TopoGraph<? extends TopoGraph.Vertex> sf ){
		super(sf, new AbstractMergeTreeNode.ComparatorValueDescending() );
	}
	
	public SplitTree( TopoGraph<? extends TopoGraph.Vertex> sf, boolean run ){
		super(sf, new AbstractMergeTreeNode.ComparatorValueDescending() );
		if( run ) this.run();
	}

}
