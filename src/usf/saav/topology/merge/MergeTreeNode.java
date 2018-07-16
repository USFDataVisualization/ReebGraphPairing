/*
 *     jPSimp - Persistence calculation and simplification of scalar fields.
 *     Copyright (C) 2016 PAUL ROSEN
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     You may contact the Paul Rosen at <prosen@usf.edu>.
 */
package usf.saav.topology.merge;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import usf.saav.topology.TopoGraph;
import usf.saav.topology.TopoTreeNode;


public abstract class MergeTreeNode implements TopoTreeNode {

	protected Vector<MergeTreeNode> children = new Vector<MergeTreeNode>( );
	protected MergeTreeNode parent  = null;
	protected MergeTreeNode partner = null;
	
	public TopoGraph.Vertex creator = null; 
	
	protected MergeTreeNode(TopoGraph.Vertex _creator) {
		this.creator = _creator;
	}

	
	public void 				addChild( MergeTreeNode c ){				  children.add(c); 					}
	public void 				addChildren(Collection<MergeTreeNode> c) { children.addAll(c);				}
	public MergeTreeNode 		getChild( int idx ){					  return children.get(idx); 		}
	public List<MergeTreeNode>	getChildren( ){ 						  return children; 					}
	public int					getChildCount( ){ 						  return children.size(); 			}
	public boolean 				isChild(MergeTreeNode node){				  return children.contains(node);	}
	public int 					childCount(){ 							  return children.size(); 			}
	public boolean 				hasChildren( ){							  return children.size() != 0; 		}
	public void 				removeChild(MergeTreeNode node){			  children.remove(node);			}

	public void setPartner( MergeTreeNode jtn ) { partner = jtn; }
	@Override public TopoTreeNode getPartner() { return partner; }
	
	@Override public float getBirth() { 
		if( partner == null ) return getValue();
		return Math.min( getValue(), partner.getValue() );
	}
	@Override public float getDeath() { 
		if( partner == null ) return Float.MAX_VALUE;
		return Math.max( getValue(), partner.getValue() );
	}
	@Override public float getPersistence() { 
		if( partner == null ) return Float.MAX_VALUE;//Float.NaN;
		return Math.abs( getValue()-partner.getValue() );
	}

	
	public void				setParent( MergeTreeNode p ){ 	parent = p;				}
	public MergeTreeNode	getParent( ){					return parent;			}
	public boolean			hasParent() {					return parent != null;	}

	
	//public abstract float getValue( );
	//public abstract int   getID( );
	
	@Override public NodeType getType() {
		switch( this.childCount() ) {
			case 0  : return NodeType.LEAF;
			case 1  : return NodeType.NONCRITICAL;
			default : return NodeType.SADDLE;
		}
	}	

	@Override public String toString(){
		StringBuffer bf = new StringBuffer( );
		toString(bf,"| ");
		return bf.toString();
	}
	
	
	private void toString( StringBuffer bf, String spaces ){
		bf.append( spaces + getID() + ": " + getValue() + "\n" );
		if( children != null ){
			for(MergeTreeNode child : children){ child.toString(bf, "  "+spaces); }
		}
	}
	
	public void toDot(StringBuffer dot_node, StringBuffer dot_edge) {
		Queue<MergeTreeNode> queue = new LinkedList<MergeTreeNode>( );
		queue.add(this);
		
		while( !queue.isEmpty() ){
			MergeTreeNode curr = queue.poll();
			dot_node.append( "\t" + curr.getID() + "[label=\"" + curr.getID() + " (" + curr.getValue() + ")\"];\n");
			for( MergeTreeNode n : curr.getChildren() ){
				dot_edge.append( "\t" + curr.getID() + " -> " + n.getID() + "\n");
			}
			queue.addAll( curr.getChildren() );
		}
		/*
		dot_node.append( "\t" + this.getPosition() + "[label=\"" + this.getPosition() + " (" + this.getValue() + ")\"];\n");
		for( JoinTreeNode n : this.getChildren() ){
			dot_edge.append( "\t" + this.getPosition() + " -> " + n.getPosition() + "\n");
			n.toDot( dot_node, dot_edge );
		}
		*/
	}
	
	/*
	public void toDot(StringBuffer dot_node, StringBuffer dot_edge, int maxdepth) {
		Queue<Pair<MergeTreeNode,Integer>> queue = new LinkedList<Pair<MergeTreeNode,Integer>>( );
		queue.add( new Pair<MergeTreeNode,Integer>(this,0) );
		
		while( !queue.isEmpty() ){
			Pair<MergeTreeNode,Integer> curr = queue.poll();
			if( curr.getSecond() < maxdepth ){
				dot_node.append( "\t" + curr.getFirst().getID() + "[label=\"" + curr.getFirst().getID() + " (" + curr.getFirst().getValue() + ")\"];\n");
				for( MergeTreeNode n : curr.getFirst().getChildren() ){
					dot_edge.append( "\t" + curr.getFirst().getID() + " -> " + n.getID() + "\n");
					queue.add( new Pair<MergeTreeNode,Integer>( n, curr.getSecond()+1 ));
				}
			}
		}
	}
	*/
 

	public static List<MergeTreeNode> findLeaves( MergeTreeNode root ) {
		List<MergeTreeNode>  ret = new Vector<MergeTreeNode>( );
		Deque<MergeTreeNode> work_stack = new ArrayDeque<MergeTreeNode>();
		
		work_stack.push(root);
		while( !work_stack.isEmpty() ){
			MergeTreeNode curr = work_stack.pop();
			if( !curr.hasChildren() ) ret.add(curr);
			for( MergeTreeNode child : curr.children )
				work_stack.push( child );
		}
		
		return ret;
	}
	
	public static void findParents( MergeTreeNode root ) {
		Deque<MergeTreeNode> work_stack = new ArrayDeque<MergeTreeNode>();
		
		work_stack.push(root);
		while( !work_stack.isEmpty() ){
			MergeTreeNode curr = work_stack.pop();
			for( MergeTreeNode child : curr.children ){
				child.setParent( curr );
				work_stack.push( child );
			}
		}
	}



	public static class ComparatorValueAscending implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			if( o1 instanceof MergeTreeNode && o2 instanceof MergeTreeNode ){
				if( ((MergeTreeNode)o1).getValue() > ((MergeTreeNode)o2).getValue() ) return  1;
				if( ((MergeTreeNode)o1).getValue() < ((MergeTreeNode)o2).getValue() ) return -1;
				if( ((MergeTreeNode)o1).getID() < ((MergeTreeNode)o2).getID() ) return  1;
				if( ((MergeTreeNode)o1).getID() > ((MergeTreeNode)o2).getID() ) return -1;
			}
			return 0;
		}	
	}

	public static class ComparatorValueDescending implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			if( o1 instanceof MergeTreeNode && o2 instanceof MergeTreeNode ){
				if( ((MergeTreeNode)o1).getValue() > ((MergeTreeNode)o2).getValue() ) return -1;
				if( ((MergeTreeNode)o1).getValue() < ((MergeTreeNode)o2).getValue() ) return  1;
				if( ((MergeTreeNode)o1).getID() < ((MergeTreeNode)o2).getID() ) return -1;
				if( ((MergeTreeNode)o1).getID() > ((MergeTreeNode)o2).getID() ) return  1;
			}
			return 0;
		}	
	}

	
	
	
}
