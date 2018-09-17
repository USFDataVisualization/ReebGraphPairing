package usf.saav.topology.merge;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import usf.saav.topology.TopoGraph;


public class AugmentedMergeTree extends AbstractAugmentedMergeTree implements Runnable {

	protected MergeTree jt;
	protected boolean operationComplete = false;
	
	protected AugmentedMergeTree( ) { }
	
	public AugmentedMergeTree( MergeTree _jt ) {
		//super(_jt.sf,_jt.comparator);
		this.jt = _jt;
	}

	public AugmentedMergeTree( MergeTree _jt, boolean run ) {
		//super(_jt.sf,_jt.comparator);
		this.jt = _jt;
		if( run ) this.run();
	}

	/*
	public AugmentedMergeTree( TopoGraph<? extends TopoGraph.Vertex> sf ) {
		//super(sf);
		jt = new MergeTree(sf,this.comparator);
	}
	*/
	
	
	public AugmentedMergeTree( TopoGraph<? extends TopoGraph.Vertex> sf, Comparator<? super JNode> comparator  ) {
		//super(sf,comparator);
		jt = new MergeTree(sf,comparator);
	}
	
	
	@Override
	public void run() {
		
		if( operationComplete ) return;

		jt.run();

		head = processTree( jt.head );
		setParents( );
		calculatePersistence();
		
		operationComplete = true;
		
	}
	
	protected AugmentedMergeTreeNode processTree( AbstractMergeTreeNode current ){
		
		
		Queue<AbstractMergeTreeNode> procQueue = new LinkedList<AbstractMergeTreeNode>();
		HashMap<AbstractMergeTreeNode,AugmentedMergeTreeNode> M2AMap = new HashMap<AbstractMergeTreeNode,AugmentedMergeTreeNode>();
		
		
		procQueue.add(current);
		
		AugmentedMergeTreeNode _head = new AugmentedMergeTreeNode( current.getID(), current.getValue(), current);
		
		AugmentedMergeTreeNode parent=_head;

		while( !procQueue.isEmpty() ) {
			AbstractMergeTreeNode top = procQueue.peek();
					
			if(top.hasParent() && top.parent.childCount()==2) 
			  {top=top.parent;		
			   parent= M2AMap.get(top);//new AugmentedMergeTreeNode( top.getID(), top.getValue(), top);
			 }
			AbstractMergeTreeNode cur = procQueue.poll();
			
			while( cur.childCount() == 1 ){
				cur = cur.getChild(0);
			}
			
			if( cur.childCount() == 0 ){
				AugmentedMergeTreeNode ret = null;
				ret = new AugmentedMergeTreeNode( cur.getID(), cur.getValue(), cur );
				grid.add( ret );
				//( childMap.containsKey(top) )
				//	childMap.get( top ).addChild( ret );
				parent.addChild(ret);
				     
			}

			if( cur.childCount() == 2 ){
				
				
				AugmentedMergeTreeNode ret = null;
				
				ret = new AugmentedMergeTreeNode( cur.getID(), cur.getValue(), cur );
				M2AMap.put(cur, ret);
				grid.add( ret );
				parent.addChild(ret);
				//if( childMap.containsKey(top) )
				//	childMap.get( top ).addChild( ret );
				
				//childMap.put( cur.getChild(0), ret );
				//childMap.put( cur.getChild(1), ret );
				
			    procQueue.add(cur.getChild(0));
				procQueue.add(cur.getChild(1));
				//parent=ret;
				
			}			
			
		}

		return _head;
		
		
	/*	
		
		AugmentedMergeTreeNode ret = null;
		while( current.childCount() == 1 ){
			current = current.getChild(0);
		}
		if( current.childCount() == 0 ){
			ret = new AugmentedMergeTreeNode( current.getID(), current.getValue(), current );
			grid.add( ret );
		}
		if( current.childCount() == 2 ){
			ret = new AugmentedMergeTreeNode( current.getID(), current.getValue(), current,
							processTree( current.getChild(0) ),
							processTree( current.getChild(1) )  );
			grid.add( ret );
		}
		
		return ret;*/
		
		
	}
	
	
	
	
}
