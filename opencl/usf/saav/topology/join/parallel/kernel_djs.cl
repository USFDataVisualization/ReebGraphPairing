
#include <common.hcl>


#define SIMP_ITERATIONS 10

typedef __global uint DisjointSet;


uint getDJS( DisjointSet * djs, uint idx );
uint getDJS( DisjointSet * djs, uint idx ) {
	return djs[idx];
}

void setDJS( DisjointSet * djs, uint idx0, uint idx1 );
void setDJS( DisjointSet * djs, uint idx0, uint idx1 ) {
	djs[idx0] = idx1;
}

uint findDJS( DisjointSet * djs, int idx );
uint findDJS( DisjointSet * djs, int idx ){
	int curSet;
	int newSet = idx;
	do {
		curSet = newSet;
		newSet = djs[curSet];
	} while( curSet != newSet );
	djs[idx] = curSet;
	return curSet;
}





void djs_init( int x, int y, int imageW, int imageH, __global float * d_input, DisjointSet * d_djs );
void djs_init( int x, int y, int imageW, int imageH, __global float * d_input, DisjointSet * d_djs ){

	if( validLocation(x,y,imageW,imageH) ){
		float largestVal = -FLT_MAX;
		int   largestIdx = -1;

		for(int _v = y-1; _v <= y+1; _v++ ){
			for(int _u = x-1; _u <= x+1; _u++ ){

				if( validLocation( _u, _v, imageW, imageH ) ){

					int   curIdx = getIndex( _u, _v, imageW, imageH );
					float curVal = getValueIdx( d_input, curIdx );

					if( curVal > largestVal ){
						largestVal = curVal;
						largestIdx = curIdx;
					}

				}

			}
		}

		setDJS( d_djs, getIndex(x,y,imageW,imageH), largestIdx );
    }

}




__kernel void djs_simplify( int x, int y, int imageW, int imageH, DisjointSet * d_djs ){

    int idx = getIndex(x,y,imageW,imageH);

    if( validLocation(x,y,imageW,imageH) ){

        bool modified = true;
        int  myIdx    = idx;
        int  curSet   = getDJS(d_djs,myIdx);
        int  newSet   = curSet;

        // These iterations help to shorten the search path to the root.
        // Each iteration is pretty expensive (1 global read, 1 global
        // write, & 1 global fence), but they can significantly reduce
        // the cost of the next phase.
        for(int i = 0; i < SIMP_ITERATIONS; i++){
    		if( modified ){
    			newSet = getDJS( d_djs, curSet );
    			setDJS( d_djs, myIdx, newSet );
    			modified = curSet != newSet;
    			curSet = newSet;
    		}
    		barrier(CLK_GLOBAL_MEM_FENCE);
        }

    	// Now, find the root of the tree, such that we
    	// can point directly to the root.
    	setDJS( d_djs, myIdx, findDJS(d_djs,curSet) );
    	barrier(CLK_GLOBAL_MEM_FENCE);

    }

}




__kernel void kernel_djs( int imageW, int imageH, __global float * d_input, __global uint  * d_djs, int phase ) {

	int x   = get_global_id(0);
    int y   = get_global_id(1);

    if( phase == 1 ) djs_init( x,y, imageW, imageH, d_input, (DisjointSet*)d_djs );
    if( phase == 2 ) djs_simplify( x,y, imageW, imageH, (DisjointSet*)d_djs );

}


