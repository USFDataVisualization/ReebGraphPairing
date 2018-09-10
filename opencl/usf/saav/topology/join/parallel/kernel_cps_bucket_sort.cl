

#include <common.hcl>
#include <heap.hcl>
#include <critical_point.hcl>
#include <bitonic_sort.hcl>
#include <histogram.hcl>


__kernel void kernel_cps_bucket( __global int * d_cps, __global int * d_histogram ) {

	int cpsN = ((*d_cps)-1)/(sizeof(GCriticalPoint)/4);
	GCriticalPoint * d_cps_in  = (GCriticalPoint*)(d_cps+1);
	GCriticalPoint * d_cps_out = d_cps_in+cpsN;

	int cpidx = get_global_id(0);

	if( cpidx >= cpsN ) return;

	GCriticalPoint* cp  = d_cps_in+cpidx;

	int bucket		 = getHistogramBucket( (GHistogram*)d_histogram, cp->value );
	int bucketBase	 = getHistogramBucketBase( (GHistogram*)d_histogram, bucket );
	int bucketOffset = incHistogramBucketOffset( (GHistogram*)d_histogram, bucket );

	move_cp_g2g( cp, d_cps_out+bucketBase+bucketOffset );

}


__kernel void kernel_cps_bucket_sort( __global int * d_cps, __global int * d_histogram ) {

	int cpsN = ((*d_cps)-1)/(sizeof(GCriticalPoint)/4);
	GCriticalPoint * d_cps_out = ((GCriticalPoint*)(d_cps+1));
	GCriticalPoint * d_cps_in  = d_cps_out+cpsN;

	int thrdIdx = get_local_id(0);
	int locW    = get_local_size(0);


	int bucket     = get_group_id(0);
	int bucketBase = getHistogramBucketBase( (GHistogram*)d_histogram, bucket );
	int bucketSize = getHistogramBucketSize( (GHistogram*)d_histogram, bucket );


	if( bucketSize > 1 ){
		sort_cps_g( thrdIdx, d_cps_in+bucketBase, bucketSize, locW );
	}

	for( int idx = thrdIdx; idx < bucketSize; idx += locW ){
		move_cp_g2g( d_cps_in+bucketBase+idx, d_cps_out+bucketBase+idx );
	}

}


