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

public class ArrayDisjointSet {

    protected int [] sets;
    
    public ArrayDisjointSet(int elemN){
        clear(elemN);
    }

    public void clear( int elemN ){
    	sets = new int[elemN];
        for(int i = 0; i < elemN; i++){
            sets[i] = i;
        }
    }

    public void invalidateSet( int elem ){
        int set = get( elem );
        if( set >= 0 ){
            sets[set] = -1;
        }
    }

    public int get( int elem ){
        while(true){
            if( elem       == -1   ) return -1;
            if( sets[elem] == -1   ) return -1;
            if( sets[elem] == elem ) return elem;
            sets[elem] = sets[ sets[elem] ];
            elem = sets[elem];
        }
    }
    
    public int find( int elem ){ return get(elem); }

    public void join( int set0, int set1 ){

        set0 = get( set0 );
        set1 = get( set1 );

        if( set0 == -1 || set1 == -1 ){
            if( set0 != -1 ) sets[set0] = -1;
            if( set1 != -1 ) sets[set1] = -1;
        }
        else{
            sets[set1] = set0;
        }
    }
    
	public int union( int set1, int set2 ){
		int r0 = find( set1 );
		int r1 = find( set2 );
		
		if ( r0 < r1 ){
			sets[r1] = r0;
			return r0;
		}
		else {
			sets[r0] = r1;
			return r1;
		}
	}
    

}
