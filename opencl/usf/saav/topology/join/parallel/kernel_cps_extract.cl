

#include <common.hcl>
#include <heap.hcl>
#include <critical_point.hcl>
#include <bitonic_sort.hcl>
#include <histogram.hcl>

void propagate_sets_local( LCriticalPoint * lcps, int lcl_idx );

void kernel_cps_extract( LCriticalPoint * lcps, __global float * d_input, __global uint * d_djs, __global int * d_cps, int imageW, int imageH, __global int * d_histogram );


__kernel void kernel_cps_extract_256( int imageW, int imageH,
										__global float * d_input,
										__global uint * d_djs,
										__global int * d_cps,
										__global int * d_histogram
										)
{

    LCriticalPoint lcps[256];
    kernel_cps_extract( lcps, d_input, d_djs, d_cps, imageW, imageH, d_histogram );

}


__kernel void kernel_cps_extract_1024( int imageW, int imageH,
										__global float * d_input,
										__global uint * d_djs,
										__global int * d_cps,
										__global  int  * d_histogram
										)
{

	LCriticalPoint lcps[1024];
    kernel_cps_extract( lcps, d_input, d_djs, d_cps, imageW, imageH, d_histogram );

}


void kernel_cps_extract( LCriticalPoint * lcps, __global float * d_input, __global uint * d_djs, __global int * d_cps, int imageW, int imageH, __global int * d_histogram ){

	int x   = get_global_id(0);
    int y   = get_global_id(1);

	int locX = get_local_id(0);
	int locY = get_local_id(1);

	int locW = get_local_size(0);
	int locH = get_local_size(1);

	int lcl_idx = locY*locW + locX;

	int lThrdW = locW*locH;


	LCriticalPoint * lCurr = lcps+lcl_idx;

	// find all cps
    if( validLocation(x,y,imageW,imageH) ){
    	cp_extract_l( d_input, d_djs, x,y,imageW,imageH, lcps+lcl_idx );
    }
    barrier(CLK_LOCAL_MEM_FENCE);

    // sort cps
    sort_cps_l( lcps, lcl_idx, lThrdW );

    // this is a quick and dirty test for early discarding of nodes
    propagate_sets_local( lcps, lcl_idx );

    // export data
	if( lCurr->setN >= 2 ){
		// update the histogram. this will be used for fast global sorting.
		//incHistogramBucket( d_histogram, (lcps+lcl_idx)->value );
		int bucket = getHistogramBucket( (GHistogram*)d_histogram, (lcps+lcl_idx)->value );
		incHistogramBucketSize( (GHistogram*)d_histogram, bucket );

	    // move the data from local memory to global memory
		Heap cps_heap = hinit( d_cps );
		move_cp_l2g( lcps+lcl_idx, (GCriticalPoint*)halloc( cps_heap, sizeof(GCriticalPoint)/4 ) );
	}

}



void propagate_sets_local( LCriticalPoint * lcps, int lcl_idx ){
	LCriticalPoint * lCurr = lcps+lcl_idx;

    for(int i = 0; i < lcl_idx; i++ ){
    	bool modified = false;
		for( int k = 1; k < lcps[i].setN; k++ ){
			int rplc = lcps[i].setID[k];
			int with = lcps[i].setID[0];

			for(int j = 0; j < lCurr->setN; j++ ){
				if( lCurr->setID[j] == rplc ){
					lCurr->setID[j] = with;
					modified = true;
				}
			}
		}
		if( modified ) set_fix_l( lCurr );
    }

}



