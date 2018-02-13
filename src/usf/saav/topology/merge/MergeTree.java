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

import usf.saav.topology.TopoGraph;
import usf.saav.topology.join.AugmentedJoinTree;
import usf.saav.topology.join.JoinTreeNode;

public class MergeTree extends AugmentedJoinTree  {
 

	/**
	 * Default Merge Tree constructor.
	 *   
	 * @param sf Scalar Field in any dimension to construct contour tree upon.
	 */
	public MergeTree( TopoGraph sf ){
		super( sf, new JoinTreeNode.ComparatorValueAscending() );
	}



	@Override
	protected AugmentedJoinTreeNode createTreeNode(int loc, float val) {
		return new MergeTreeNode(loc,val);
	}

	@Override
	protected AugmentedJoinTreeNode createTreeNode(int loc, float val, AugmentedJoinTreeNode c0, AugmentedJoinTreeNode c1) {
		return new MergeTreeNode(loc,val,c0,c1);
	}
	
	
	
	public class MergeTreeNode extends AugmentedJoinTreeNode {
		
		public MergeTreeNode( int loc, float val ){
			super(loc,val); 
		}
		
		public MergeTreeNode( int loc, float val, AugmentedJoinTreeNode c0, AugmentedJoinTreeNode c1 ){
			super(loc,val,c0,c1);
		}
		
		public NodeType getType() {
			if( this.getChildCount() == 0 ) return NodeType.LEAF_MIN;
			if( this.getChildCount() >= 2 ) return NodeType.MERGE;
			return NodeType.UNKNOWN;
		}
		

	}

	
	
}
