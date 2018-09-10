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

public class ZOrder {


	public static long getOrdered3( long x, long y, long z ){
		return partition3(x) | (partition3(y)<<1) | (partition3(z)<<2); 
	}

	public static long getOrdered3( long [] xyz ){
		return partition3(xyz[0]) | (partition3(xyz[1])<<1) | (partition3(xyz[2])<<2); 
	}

	public static long [] getPosition3( long elem ){
		return new long[]{ unpartition3(elem), unpartition3(elem>>1), unpartition3(elem>>2) };
	}

	
	
	
	public static long getOrdered2( long x, long y ){
		return partition2(x) | (partition2(y)<<1);
	}

	public static long getOrdered2( long [] xy ){
		return partition2(xy[0]) | (partition2(xy[1])<<1);
	}

	public static long [] getPosition2( long elem ){
		return new long[]{ unpartition2(elem), unpartition2(elem>>1) };
	}

	
	private static final long B2[] = {0x5555555555555555L, 0x3333333333333333L, 0x0F0F0F0F0F0F0F0FL, 0x00FF00FF00FF00FFL, 0x0000FFFF0000FFFFL, 0x00000000FFFFFFFFL};
	private static final long S2[] = {1, 2, 4, 8, 16, 32 };

	
	
	private static long partition2( long x ){
		x = (x | (x << S2[4])) & B2[4];
		x = (x | (x << S2[3])) & B2[3];
		x = (x | (x << S2[2])) & B2[2];
		x = (x | (x << S2[1])) & B2[1];
		x = (x | (x << S2[0])) & B2[0];
		return x;
	}
	
	public static long unpartition2( long n){
        n = n & B2[0];
        n = (n ^ (n >> S2[0])) & B2[1];
        n = (n ^ (n >> S2[1])) & B2[2];
        n = (n ^ (n >> S2[2])) & B2[3];
        n = (n ^ (n >> S2[3])) & B2[4];
        n = (n ^ (n >> S2[4])) & B2[5];
        return n;
	}
	
	
	private static final long B3[] = { 0x9249249249249249L, 0x30c30c30c30c30c3L, 0xf00f00f00f00f00fL, 0x00ff0000ff0000ffL, 0xffff00000000ffffL };
	private static final long S3[] = { 2, 4, 8, 16, 32 };
	

	private static long partition3( long x ){
		x = (x | (x << S3[3])) & B3[3];
		x = (x | (x << S3[2])) & B3[2];
		x = (x | (x << S3[1])) & B3[1];
		x = (x | (x << S3[0])) & B3[0];
        return x;
	}	
	

	
	private static long unpartition3( long n){
        n = n & B3[0];
        n = (n ^ (n >> S3[0])) & B3[1];
        n = (n ^ (n >> S3[1])) & B3[2];
        n = (n ^ (n >> S3[2])) & B3[3];
        n = (n ^ (n >> S3[3])) & B3[4];
        return n;
	}
	
	

}
