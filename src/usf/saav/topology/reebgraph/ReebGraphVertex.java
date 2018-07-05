package usf.saav.topology.reebgraph;

import java.util.ArrayList;

import usf.saav.topology.TopoGraph;
import usf.saav.topology.TopoGraph.Vertex;
import usf.saav.topology.TopoTreeNode;

public class ReebGraphVertex implements TopoGraph.Vertex, TopoTreeNode {
	private float val;
	private int idx;
	private int gid;
	private float realVal;
	
	private ReebGraphVertex topoPartner;
	public ArrayList<ReebGraphVertex> neighbors = new ArrayList<ReebGraphVertex>();

	public ReebGraphVertex( float _val, float _realVal, int _gid ) {
		val = _val;
		//id = _id;
		gid = _gid;
		realVal = _realVal;
	}

	public String toString(){
		return getGlobalID() + "/" + idx + " (" + getRealValue() + "/" + value() + ")";
	}

	@Override
	public Vertex [] neighbors() {
		return neighbors.toArray( new Vertex[neighbors.size()] );
	}
	
	@Override public float value() { return val; }
	@Override public int getID() { return idx; }
	public void setID(int i) { idx = i; }
	public int getGlobalID() { return gid; }
	public float getRealValue() { return realVal; }
	public void setValue(float v) { val = v; }

	@Override 
	public NodeType getType() {
		int cntLess=0;
		int cntMore=0;
		for( ReebGraphVertex n : neighbors ) {
			if(value()<n.value()) cntLess++;		
			if(value()>n.value()) cntMore++;		
		}
		if( cntLess==0 ) return NodeType.LEAF_MAX;
		if( cntMore==0 ) return NodeType.LEAF_MIN;
		if( cntLess==2 ) return NodeType.UPFORK;
		if( cntMore==2 ) return NodeType.DOWNFORK;
		return null;
	}


	public void addNeighbor(ReebGraphVertex v){
		neighbors.add(v);
	}
	
	public static void setNeighbors(ReebGraphVertex v0, ReebGraphVertex v1) {
		v0.addNeighbor(v1);
		v1.addNeighbor(v0);
	}

	@Override public TopoTreeNode getPartner() { return topoPartner; }
	public void setPartner( ReebGraphVertex p ) { topoPartner = p; }

	
	public boolean isEssential() {
		return (getType() == NodeType.DOWNFORK && topoPartner.getType() == NodeType.UPFORK )
				|| (getType() == NodeType.UPFORK && topoPartner.getType() == NodeType.DOWNFORK );
	}

	@Override public float getBirth() {
		if( topoPartner == null ) 
			return value();
		if( isEssential() ) 
			return Math.max( value(), topoPartner.value() ); 
		return Math.min( value(), topoPartner.value() ); 
	}

	@Override 
	 public float getDeath() { 
		if( topoPartner == null ) 
			return Float.POSITIVE_INFINITY;
		if( isEssential() ) 
			return Math.min( value(), topoPartner.value() );
		return Math.max( value(), topoPartner.value() ); 
	}

	@Override public float getPersistence() { return getDeath()-getBirth(); }




}
