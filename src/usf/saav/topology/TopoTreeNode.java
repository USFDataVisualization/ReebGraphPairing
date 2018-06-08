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
package usf.saav.topology;
 
import java.util.Comparator;

public interface TopoTreeNode {


	public enum NodeType {
		LEAF_MIN, LEAF_MAX, DOWNFORK, UPFORK, LEAF, SADDLE, NONCRITICAL, UNKNOWN
	}
	
	
	NodeType getType();

	TopoTreeNode getPartner();

	int getID();

	float getBirth();
	float getDeath();
	float getPersistence();

	
	
	public static class ComparePersistenceAscending implements Comparator<TopoTreeNode> {
		@Override public int compare(TopoTreeNode o1, TopoTreeNode o2) {
			if( o1.getPersistence() > o2.getPersistence() ) return  1;
			if( o1.getPersistence() < o2.getPersistence() ) return -1;
			return 0;
		}
	}

	public static class ComparePersistenceDescending implements Comparator<TopoTreeNode> {
		@Override public int compare(TopoTreeNode o1, TopoTreeNode o2) {
			if( o1.getPersistence() < o2.getPersistence() ) return  1;
			if( o1.getPersistence() > o2.getPersistence() ) return -1;
			return 0;
		}
	}

	
}

