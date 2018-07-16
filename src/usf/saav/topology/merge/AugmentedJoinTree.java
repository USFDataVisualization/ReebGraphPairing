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

public class AugmentedJoinTree extends AugmentedMergeTree  {
 
	/**
	 * Default Join Tree constructor.
	 *   
	 * @param sf Scalar Field in any dimension to construct contour tree upon.
	 */
	public AugmentedJoinTree( TopoGraph<? extends TopoGraph.Vertex> sf ){
		super( sf, new MergeTreeNode.ComparatorValueAscending() );
	}

	public AugmentedJoinTree( TopoGraph<? extends TopoGraph.Vertex> sf, boolean run ){
		super( sf, new MergeTreeNode.ComparatorValueAscending() );
		if( run ) this.run();
	}

}
