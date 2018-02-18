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
package usf.saav.topology.join;

import java.util.Comparator;

import usf.saav.topology.TopoGraph;
import usf.saav.topology.TopoTree;
import usf.saav.topology.TopoTreeNode;
import usf.saav.topology.join.JoinTree.Node;

public abstract class AugmentedJoinTree extends AugmentedJoinTreeBase implements TopoTree, Runnable {
	
	protected AugmentedJoinTreeNode global_extreme;
	protected TopoGraph cl;
	
	private Comparator<? super Node> comparator;

	
	protected AugmentedJoinTree( TopoGraph cl, Comparator<? super Node> comparator ){
		this.cl = cl;
		this.comparator = comparator;
	}
	
	@Override
	public void run() {
		// Build a join tree.
		JoinTree jt = new JoinTree( cl, comparator );
		jt.run();

		head = processTree( jt.getRoot() );
		
		calculatePersistence();
		
		for(int i = 0; i < size(); i++){
			float per = getPersistence(i);
			if( Float.isNaN(per) )
				global_extreme = (AugmentedJoinTreeNode) getNode(i);
		}
	}
	


	
	protected AugmentedJoinTreeNode processTree( JoinTreeNode current ){
		
		while( current.childCount() == 1 ){
			current = current.getChild(0);
		}
		if( current.childCount() == 0 ){
			nodes.add( createTreeNode( current.getID(), current.getValue() ) );
			return (AugmentedJoinTreeNode)nodes.lastElement();
		}
		if( current.childCount() == 2 ){
			nodes.add( createTreeNode( current.getID(), current.getValue(),
							processTree( current.getChild(0) ),
							processTree( current.getChild(1) ) 
						) );
			return (AugmentedJoinTreeNode)nodes.lastElement();
		}
		// Monkey saddle --- should probably do something a little smarter here
		if( current.childCount() == 3 ){
			AugmentedJoinTreeNode child = createTreeNode( current.getID(), current.getValue(),
														processTree( current.getChild(0) ),
														processTree( current.getChild(1) ) 
													);
			AugmentedJoinTreeNode parent = createTreeNode( current.getID(), current.getValue(),
														child,
														processTree( current.getChild(2) ) 
				);
			nodes.add(child);
			nodes.add(parent);
			
			return parent;
		}
		// 4-way saddle --- yicks!
		if( current.childCount() == 4 ){
			AugmentedJoinTreeNode child1 = createTreeNode( current.getID(), current.getValue(),
														processTree( current.getChild(0) ),
														processTree( current.getChild(1) ) 
													);
			AugmentedJoinTreeNode child0 = createTreeNode( current.getID(), current.getValue(),
														child1,
														processTree( current.getChild(2) ) 
				);

			AugmentedJoinTreeNode parent = createTreeNode( current.getID(), current.getValue(),
														child0,
														processTree( current.getChild(3) ) 
				);

			nodes.add(child1);
			nodes.add(child0);
			nodes.add(parent);
			
			return parent;
		}
		
		return null;
	}
	
	public AugmentedJoinTreeNode getGlobalExtreme(){ return global_extreme; }

	
	public abstract class AugmentedJoinTreeNode extends JoinTreeNode implements TopoTreeNode {
		
		private int   location;
		private float value;
		
		
		protected AugmentedJoinTreeNode( int loc, float val ){
			this.location = loc;
			this.value = val;
		}
		
		protected AugmentedJoinTreeNode( int loc, float val, AugmentedJoinTreeNode c0, AugmentedJoinTreeNode c1 ){
			this.location = loc;
			this.value = val;
			this.addChild(c0);
			this.addChild(c1);
		}
		
		
		@Override public int	getID() { return location; }
		@Override public float	getValue() { 	return value;	 }

	}
	
	protected abstract AugmentedJoinTreeNode createTreeNode( int loc, float val );
	protected abstract AugmentedJoinTreeNode createTreeNode( int loc, float val, AugmentedJoinTreeNode c0, AugmentedJoinTreeNode c1 );

	

}
