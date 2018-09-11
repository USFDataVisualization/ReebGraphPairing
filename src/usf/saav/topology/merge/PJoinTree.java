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

import usf.saav.common.jocl.joclDevice;
import usf.saav.scalarfield.ScalarField2D;
import usf.saav.topology.TopoGraph;

public class PJoinTree extends PAugmentedMergeTree {

	public PJoinTree(joclDevice _device) {
		super(_device);
	}
	
	@Override
	public void calculate( ScalarField2D _sf ){
		calculate(_sf,true);
		//calculatePersistence();
	}
	
	@Override
	public void calculate( TopoGraph<?> _sf ){
		calculate(_sf,true);
		//calculatePersistence();
	}


	@Override
	protected PAugmentedMergeTreeNode createTreeNode(int sf_node) {
		return new PMergeTreeNode( sf_node );
	}
	
	public class PMergeTreeNode extends PAugmentedMergeTreeNode {

		PMergeTreeNode(int idx) {
			super(idx);
		}

		@Override
		public NodeType getType() {
			if( this.childCount() == 0 )
				return NodeType.LEAF_MIN;
			return NodeType.SADDLE;
		}

		@Override
		public int getID() {
			return super.idx;
		}


	}


}
