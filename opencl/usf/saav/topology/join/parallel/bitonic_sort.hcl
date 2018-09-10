#ifndef __BITONIC_SORT_HCL__
#define __BITONIC_SORT_HCL__

void compare_and_swap_g( __global void * data, int idx0, int idx1 );
void compare_and_swap_g( __global void * data, int idx0, int idx1 ){
	GCriticalPoint* m0 = (GCriticalPoint*)data+idx0;
	GCriticalPoint* m1 = (GCriticalPoint*)data+idx1;
	if( m0->value <  m1->value ){
		swap_cp_g( m0, m1 );
	}
}

void compare_and_swap_l( __local void * data, int idx0, int idx1 );
void compare_and_swap_l( __local void * data, int idx0, int idx1 ){
	LCriticalPoint* m0 = (LCriticalPoint*)data+idx0;
	LCriticalPoint* m1 = (LCriticalPoint*)data+idx1;
	if( m0->value <  m1->value ){
		swap_cp_l( m0, m1 );
	}
}




void bitonic_sorter_g( __global void* data, int idx, int phase, int listsize, bool firstPass );
void bitonic_sorter_g( __global void* data, int idx, int phase, int listsize, bool firstPass ){

	int offset = (idx%(1<<phase));
	int thisGrp = (idx-offset)*2;
	int nextGrp = thisGrp + (2<<phase);

	int idx0, idx1;

	idx0 = thisGrp + offset;
	if( firstPass ){
		idx1 = nextGrp - offset - 1;
	}
	else{
		idx1 = thisGrp + offset + (1<<phase);
	}

	if( idx1 < listsize ){
		compare_and_swap_g( data, idx0, idx1 );
	}

}


void sort_cps_g( int idx, GCriticalPoint* mil, int listsize, int thrdW );
void sort_cps_g( int idx, GCriticalPoint* mil, int listsize, int thrdW ){
	int iter = ilog2(listsize)+2;

	for( int i = 0; i < iter; i++ ){
		for(int j = i; j >= 0; j-- ){
			for(int curIdx = idx; curIdx < listsize; curIdx+=thrdW ){
				bitonic_sorter_g( mil, curIdx, j, listsize, i==j );
			}
			barrier(CLK_GLOBAL_MEM_FENCE);
		}
	}

}



void bitonic_sorter_l( __local void* data, int idx, int phase, int listsize, bool firstPass );
void bitonic_sorter_l( __local void* data, int idx, int phase, int listsize, bool firstPass ){

	int offset = (idx%(1<<phase));
	int thisGrp = (idx-offset)*2;
	int nextGrp = thisGrp + (2<<phase);

	int idx0, idx1;

	idx0 = thisGrp + offset;
	if( firstPass ){
		idx1 = nextGrp - offset - 1;
	}
	else{
		idx1 = thisGrp + offset + (1<<phase);
	}

	if( idx1 < listsize ){
		compare_and_swap_l( data, idx0, idx1 );
	}

}


void sort_cps_l( __local void* data, int idx, int listsize );
void sort_cps_l( __local void* data, int idx, int listsize ){
	int iter = ilog2(listsize)+2;

	for( int i = 0; i < iter; i++ ){
		for(int j = i; j >= 0; j-- ){
			bitonic_sorter_l( data, idx, j, listsize, i==j );
			barrier(CLK_LOCAL_MEM_FENCE);
		}
	}

}


#endif
