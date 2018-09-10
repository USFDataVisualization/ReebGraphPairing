

#include <common.hcl>
#include <heap.hcl>
#include <critical_point.hcl>


void kernel_cps_propagate_merges_phase_1( GCriticalPoint* gCurr, int cpIdx, int cpsN, __global int * d_locks );
void kernel_cps_propagate_merges_phase_1( GCriticalPoint* gCurr, int cpIdx, int cpsN, __global int * d_locks ){

	if( cpIdx < cpsN ){
		set_fix_g( gCurr );
		for(int i = 1; i < gCurr->ref; i++ ){
			atomic_min( &(d_locks[gCurr->setID[i]]), cpIdx );
		}
	}

}


void kernel_cps_propagate_merges_phase_2( LCriticalPoint * lCurr, GCriticalPoint* gCurr, int cpIdx, int cpsN, GCriticalPoint* cps, __global int * d_locks, __global int * modification_counter, __global int * min_proc );
void kernel_cps_propagate_merges_phase_2( LCriticalPoint * lCurr, GCriticalPoint* gCurr, int cpIdx, int cpsN, GCriticalPoint* cps, __global int * d_locks, __global int * modification_counter, __global int * min_proc ){

	bool modified = false;

	if( cpIdx < cpsN ){
		move_cp_g2l( cps+cpIdx, lCurr );
		for(int i = 0; i < lCurr->setN; i++ ){

			int owner    = cpIdx;
			int newOwner = d_locks[lCurr->setID[i]];

			while( newOwner < owner ){
				owner = newOwner;

				int oldsid = lCurr->setID[i];
				int newsid = (cps+newOwner)->setID[0];

				lCurr->setID[i] = newsid;

				modified = modified || oldsid != newsid;

				newOwner = d_locks[newsid];
			}

		}
		set_fix_l( lCurr );
	}

	if( modified ){
		move_cp_l2g( lCurr, cps+cpIdx );
		for(int i = 1; i < lCurr->setN; i++ ){
			atomic_min( &(d_locks[lCurr->setID[i]]), cpIdx );
		}
		atomic_inc( modification_counter );
		atomic_min( min_proc, cpIdx );
	}
}




__kernel void kernel_cps_propagate_256( __global int * d_cps, __global int * d_locks, int phase, __global int * d_mod_counter, int cpsOffset ){

	int gThrdIdx = get_global_id(0);
	int lThrdIdx = get_local_id(0);

    LCriticalPoint lcps[256];
	LCriticalPoint * lCurr = lcps+lThrdIdx;

	int cpIdx = cpsOffset + gThrdIdx;
	int cpsN  = ((*d_cps)-1)/(sizeof(GCriticalPoint)/4);

	GCriticalPoint* cps   = (GCriticalPoint*)(d_cps+1);
	GCriticalPoint* gCurr = cps+cpIdx;

	__global int * modified_counter = d_mod_counter + (phase-1)*2;

    if( phase == 1 ) kernel_cps_propagate_merges_phase_1( gCurr, cpIdx, cpsN, d_locks );
    if( phase >= 2 ) kernel_cps_propagate_merges_phase_2( lCurr, gCurr, cpIdx, cpsN, cps, d_locks, modified_counter, modified_counter+1 );

}

__kernel void kernel_cps_propagate_1024( __global int * d_cps, __global int * d_locks, int phase, __global int * d_mod_counter, int cpsOffset ){

	int gThrdIdx = get_global_id(0);
	int lThrdIdx = get_local_id(0);

    LCriticalPoint lcps[1024];
	LCriticalPoint * lCurr = lcps+lThrdIdx;

	int cpIdx = cpsOffset + gThrdIdx;
	int cpsN  = ((*d_cps)-1)/(sizeof(GCriticalPoint)/4);

	GCriticalPoint* cps   = (GCriticalPoint*)(d_cps+1);
	GCriticalPoint* gCurr = cps+cpIdx;

	__global int * modified_counter = d_mod_counter + (phase-1)*2;

    if( phase == 1 ) kernel_cps_propagate_merges_phase_1( gCurr, cpIdx, cpsN, d_locks );
    if( phase >= 2 ) kernel_cps_propagate_merges_phase_2( lCurr, gCurr, cpIdx, cpsN, cps, d_locks, modified_counter, modified_counter+1 );

}

