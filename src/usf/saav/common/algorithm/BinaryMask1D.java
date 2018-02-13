/*
 *     saav-core - A (very boring) software development support library.
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
package usf.saav.common.algorithm;

import java.util.Arrays;


/**
 * Used for creating a binary (on/off) mask in 1D
 */
public class BinaryMask1D {
	
	/** the mask */
	private boolean [] mask;
	
	/**
	 * Instantiates a new mask
	 *
	 * @param width the width of the mask
	 * @param default_val the default value to set the mask
	 */
	public BinaryMask1D( int width, boolean default_val ){
		mask = new boolean[width];
		Arrays.fill(mask, default_val);
	}

	/**
	 * Sets an element in the mask to true/on
	 *
	 * @param x the x
	 */
	public void set( int x ){ mask[ x ] = true; }
	
	/**
	 * Sets an element in the mask to false/off
	 *
	 * @param x the x
	 */
	public void clear( int x ){ mask[ x ] = false; }
	
	/**
	 * Checks if an element is sets
	 *
	 * @param x the x
	 * @return true, if is sets the
	 */
	public boolean isSet( int x ){ return mask[ x ]; }

	/* (non-Javadoc)
	 * @see usf.saav.alma.algorithm.Surface1D#size()
	 */
	public int getWidth() { return mask.length; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override 
	public String toString( ){
		String ret = "";
		for( int i = 0; i < mask.length; i++){
			ret += ( mask[i] ? "1 " : "0 " );
			if( (i%mask.length)==(mask.length-1) ) ret += "\n";
		}
		return ret;
	}


}
