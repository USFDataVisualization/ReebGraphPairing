


__kernel void kernel_cps_sort_256(__global int * d_cps ) {

	int thrdIdx = get_global_id(0);
	int locW    = get_local_size(0);

	GCriticalPoint* cps = (GCriticalPoint*)(d_cps+1);

	int cnt = ((*d_cps)-1)/(sizeof(GCriticalPoint)/4);

	sort_cps_g( thrdIdx, cps, cnt, locW );

}


__kernel void kernel_cps_sort_1024( __global int * d_cps ) {

	int thrdIdx = get_global_id(0);
	int locW    = get_local_size(0);

	GCriticalPoint* cps = (GCriticalPoint*)(d_cps+1);

	int cnt = ((*d_cps)-1)/(sizeof(GCriticalPoint)/4);

	sort_cps_g( thrdIdx, cps, cnt, locW );
}





/*

void fixSets( GCriticalPoint * curr );

void fixSets( GCriticalPoint * curr ){
	int ref = curr->ref;
	curr->ref = 0;
	for(int i = 0; i < ref; i++ ){
		for( int j = i+1; j < ref; j++ ){
			if( curr->setID[i] <  curr->setID[j] ){
				swap_int( &(curr->setID[i]), &(curr->setID[j]) );
			}
			else if( curr->setID[i] == curr->setID[j] ){
				curr->setID[j] = -1;
			}
		}
		if( curr->setID[i] >= 0 ){
			curr->ref++;
		}
	}
}


void cp_invalidate_g( GCriticalPoint * gcp );
void cp_invalidate_g( GCriticalPoint * gcp ){
	gcp->value = -FLT_MAX;
   	gcp->ref  = -1;
   	gcp->location = -1;

	for(int i = 0; i < 8; i++){
		gcp->setID[i] = -1;
	}

}

void cp_invalidate_l( LCriticalPoint * lcp );
void cp_invalidate_l( LCriticalPoint * lcp ){
	lcp->value = -FLT_MAX;
	lcp->setN  = -1;
   	lcp->location = -1;

	for(int i = 0; i < 6; i++){
		lcp->setID[i] = -1;
	}
}

*/




/*
int getCriticalPointType( __global float * data, int u, int v, int w, int h );

int getCriticalPointType( __global float * data, int u, int v, int w, int h ){

	float myVal = getValue( data, u, v, w, h );

	float ring[8];
	int   iring[8];
	for( int i = 0; i < 8; i++ ){
		int cu = u+neighborU[i];
		int cv = v+neighborV[i];

		if( validLocation( cu, cv, w, h ) ) {
			ring[i]  = getValue( data, cu, cv, w, h );
			iring[i] = (ring[i]<myVal) ? -1 : 1;
		}
		else {
			ring[i]  = myVal;
			iring[i] = 0;
		}
	}

	bool boundary = false;
	bool above = false;
	bool below = false;
	int transition = 0;
	for(int i = 0; i < 8; i++){
		if (iring[i]<0){ below = true; }
		if (iring[i]>0){ above = true; }

		if( iring[i] == 0 || iring[(i+1)%8] == 0){
			boundary = true;
		}
		else if( iring[i] != iring[(i+1)%8] )
			transition++;
	}


	if( below && transition == 0 ) return MAXIMA;
	if( above && transition == 0 ) return MINIMA;
	if(!boundary && transition >= 4 ) return SADDLE;
	if( boundary && transition >= 2 ) return SADDLE;
	return NORMAL;

}




GCriticalPoint * createInvalidCP( Heap heap_cps, int idx );

GCriticalPoint * createInvalidCP( Heap heap_cps, int idx ){

	GCriticalPoint * newCP = 0;
	newCP = (GCriticalPoint *)halloc( heap_cps, sizeof(GCriticalPoint)/4 ) ;

	newCP->location = idx;
	newCP->value    = -FLT_MAX;
	newCP->ref      = -1;
	//newCP->type     = NORMAL;

	for(int i = 0; i < 8; i++){
		newCP->setID[i] = -1;
	}

	return newCP;
}


bool groupRemove( __global int * set, int val );

bool groupRemove( __global int * set, int val ){
	bool removed = false;
	int j = 0;
	for(int i = 0; i < 8; i++){
		set[j] = set[i];

		if( set[i] == val ){
			removed = true;
		}
		else{
			j++;
		}

		if( set[i] == -1 ) return removed;
	}
	return removed;

}

bool groupInsert( __global int * set, int val );

bool groupInsert( __global int * set, int val ){
	for(int i = 0; i < 8; i++){
		// value already exists, do nothing
		if( set[i] == val ){
			return false;
		}
		// reached the end of the list, insert
		if( set[i] == -1 ){
			set[i] = val;
			return true;
		}
		// this will sort points, basically bubble sort
		if( set[i] > val ){
			int tmp = set[i];
			set[i] = val;
			val = tmp;
		}
	}
	return false;
}

*/



/*

void set_update_l( LCriticalPoint * curr, __global uint * d_djs );
void set_update_l( LCriticalPoint * curr, __global uint * d_djs ){
	for(int i = 0; i < curr->setN; i++){
		curr->setID[i] = findDJS( d_djs, curr->setID[i] );
	}
}


*/


/*

void cp_extract_l( __global float * data, __global uint * djs, int u, int v, int w, int h, LCriticalPoint * cp_loc );
void cp_extract_l( __global float * data, __global uint * djs, int u, int v, int w, int h, LCriticalPoint * cp_loc ){

	int  cp_type  = getCriticalPointType( data,u,v,w,h );
	//int cp_type = SADDLE;
	float myVal	  = getValue( data,u,v,w,h );

	int myIdx = getIndex( u,v,w,h );
	int mySID = *(djs+myIdx);
	//int mySID = findDJS( djs, getIndex( u,v,w,h) );

	//int cp_type = ( myIdx != mySID ) ? SADDLE : MAXIMA ;



	cp_init_l( cp_loc );

	if( cp_type == SADDLE ){
		cp_loc->location = getIndex( u,v,w,h );
		cp_loc->value    = myVal;

		for(int i = 0; i < 8; i++){
			int _u = u+neighborU[i];
			int _v = v+neighborV[i];
			if( validLocation( _u, _v, w, h ) ){
				float otherVal = getValue( data, _u,_v, w, h );
				if( otherVal >= myVal ){
					int sid = findDJS( djs, getIndex(_u,_v,w,h) );
					set_insert_l( cp_loc, sid );
				}
			}
		}
	}


}

*/



/*

void set_fix2_g( GCriticalPoint * curr );
void set_fix2_g( GCriticalPoint * curr ){
	int setN = curr->ref;
	curr->ref = 0;
	for(int i = 0; i < setN; i++ ){
		for( int j = i+1; j < setN; j++ ){
			if( curr->setID[i] < curr->setID[j] ){
				swap_int( &(curr->setID[i]), &(curr->setID[j]) );
			}
		}
	}

	int j = 1;
	for(int i = 1; i < setN; i++ ){
		if( curr->setID[j-1] != curr->setID[i] ){
			curr->setID[j] = curr->setID[i];
			j++;
		}
	}
	curr->ref = j;
	for( ; j < setN; j++ ){
		curr->setID[j] = -1;
	}

}

*/



#ifndef __HASHMAP_HCL__
#define __HASHMAP_HCL__

#ifndef MAX_HASH_SIZE
	#define MAX_HASH_SIZE 4096
#endif
#ifndef HASH_BINS
	#define HASH_BINS 64
#endif


typedef struct tag_hashrecord {
	int value;
	int data;
	int next;
} HashRecord ;


typedef struct tag_hashtable {
	int heappnt;
	int sets[HASH_BINS];
	HashRecord records[(MAX_HASH_SIZE-HASH_BINS-1)/3];
} HashTable;


/*
typedef struct tag_hashtable {
	int heappnt;
	short sets[HASH_BINS];
	int value[1024];
	int data[1024];
	short next[1024];
} HashTable;
*/

typedef __local HashTable LHashTable;
typedef __global HashTable GHashTable;

/*
bool hash_aquire_lock_l( LHashTable * hash_table, int set ){
	return atomic_xchg( &(hash_table->locks[set]), 1 ) == 0;
}

void hash_release_lock_l( LHashTable * hash_table, int set ){
	atomic_xchg( &(hash_table->locks[set]), 0 );
}
*/

void hash_init_l( LHashTable * hash_table, int thrdIdx );
void hash_init_l( LHashTable * hash_table, int thrdIdx ){
	if( thrdIdx == 0 ){
		hash_table->heappnt = 0;
	}
	if( thrdIdx < HASH_BINS ){
		hash_table->sets[thrdIdx]  = -1;
//		hash_table->locks[thrdIdx] =  0;
	}
}

void hash_init_g( GHashTable * hash_table, int thrdIdx );
void hash_init_g( GHashTable * hash_table, int thrdIdx ){
	if( thrdIdx == 0 ){
		hash_table->heappnt = 0;
	}
	if( thrdIdx < HASH_BINS ){
		hash_table->sets[thrdIdx]  = -1;
//		hash_table->locks[thrdIdx] =  0;
	}
}

void hash_put_l( LHashTable * hash_table, int value, int data );
void hash_put_l( LHashTable * hash_table, int value, int data ){
	int set = abs(value)%HASH_BINS;
	int offset = atomic_inc( &(hash_table->heappnt) );

	hash_table->records[offset].value = value;
	hash_table->records[offset].data  = data;
	hash_table->records[offset].next  = atomic_xchg( &(hash_table->sets[set]), offset );
}

void hash_put_g( GHashTable * hash_table, int value, int data );
void hash_put_g( GHashTable * hash_table, int value, int data ){
	int set = abs(value)%HASH_BINS;
	int offset = atomic_inc( &(hash_table->heappnt) );

	hash_table->records[offset].value = value;
	hash_table->records[offset].data  = data;
	hash_table->records[offset].next  = atomic_xchg( &(hash_table->sets[set]), offset );
}

int hash_get_l( LHashTable * hash_table, int value );
int hash_get_l( LHashTable * hash_table, int value ){
	int set = abs(value)%HASH_BINS;
	int cur = hash_table->sets[set];

	while( cur >= 0 ){
		if( value == hash_table->records[cur].value ){
			return hash_table->records[cur].data;
		}
		cur = hash_table->records[cur].next;
	}
	return INT_MAX;
}

int hash_get_g( GHashTable * hash_table, int value );
int hash_get_g( GHashTable * hash_table, int value ){
	int set = abs(value)%HASH_BINS;
	int cur = hash_table->sets[set];
	int loop = 0;
	while( cur >= 0 ){
		if( value == hash_table->records[cur].value ){
			return hash_table->records[cur].data;
		}
		cur = hash_table->records[cur].next;
	}
	return INT_MAX;
}

bool hash_set_if_exists_g( GHashTable * hash_table, int value, int data );
bool hash_set_if_exists_g( GHashTable * hash_table, int value, int data ){
	int set = abs(value)%HASH_BINS;
	int cur = hash_table->sets[set];
	int loop = 0;
	while( cur >= 0 ){
		if( value == hash_table->records[cur].value ){
			hash_table->records[cur].data = data;
			return true;
		}
		cur = hash_table->records[cur].next;
	}
	// value does not exist...
	return false;
}

void hash_set_g( GHashTable * hash_table, int value, int data );
void hash_set_g( GHashTable * hash_table, int value, int data ){

	if( !hash_set_if_exists_g( hash_table, value, data ) ){
		// value does not exist...
		hash_put_g( hash_table, value, data );
	}
}

int hash_atomic_min( LHashTable * hash_table, int value, int data );
int hash_atomic_min( LHashTable * hash_table, int value, int data ){
	int set = abs(value)%HASH_BINS;
	int cur = hash_table->sets[set];

	while( cur >= 0 ){
		if( value == hash_table->records[cur].value ){
			return atomic_min( &(hash_table->records[cur].data), data );
		}
		cur = hash_table->records[cur].next;
	}
	return INT_MAX;
}



int hash_djs_find_g( GHashTable * hash_table, int value );
int hash_djs_find_g( GHashTable * hash_table, int value ){
	int prv;
	int cur = value;
	do {
		prv = cur;
		cur = hash_get_g( hash_table, prv );
		if( prv == cur ) return prv;
		if( cur == INT_MAX ) return prv;
	} while( prv != cur && cur != INT_MAX );
	return prv;
}



#endif


void extract_find_cps_l( LCriticalPoint * lcps, int lcl_idx, int x, int y, int imgW, int imgH, __global float * d_input, __global uint * d_djs );
void extract_remove_edges_l( LCriticalPoint * lcps, int lcl_idx );
void extract_count_valid_l( LCriticalPoint * lcps, int lcl_idx, __local int * ttl_active );
void extract_invalidate_inactive_l( LCriticalPoint * lcps, int lcl_idx, int cnt );




void extract_find_cps_l( LCriticalPoint * lcps, int lcl_idx, int x, int y, int imgW, int imgH, __global float * d_input, __global uint * d_djs ){
    if( validLocation(x,y,imgW,imgH) ){
    	cp_extract_l( d_input, d_djs, x,y,imgW,imgH, lcps+lcl_idx );
    }
    barrier(CLK_LOCAL_MEM_FENCE);
}


/*
void extract_remove_edges_l( LCriticalPoint * lcps, int cnt, int lcl_idx ){
    for(int i = 0; i < cnt; i++ ){
       	if( lcps[i].setN >= 2 ){

       		for( int k = 1; k < lcps[i].setN; k++ ){
       			int rplc = (lcps+i)->setID[k];
       			int with = (lcps+i)->setID[0];
       			int proc_idx = i+1 + lcl_idx;

   				if( proc_idx < cnt ){

   					for( int j = 0; j < (lcps+proc_idx)->setN; j++ ){
   						if( (lcps+proc_idx)->setID[j] == rplc ){
   							(lcps+proc_idx)->setID[j] = with;
   						}
   					}

   					set_fix_l( (lcps+proc_idx) );
   				}

   			}
       	}

   	    barrier(CLK_LOCAL_MEM_FENCE);
    }
}
*/


void extract_count_valid_l( LCriticalPoint * lcps, int lcl_idx, __local int * ttl_active ){
	int cnt = *ttl_active;
    barrier(CLK_LOCAL_MEM_FENCE);
    if( lcl_idx < cnt && (lcps+lcl_idx)->setN <= 0 ){
    	atomic_dec( ttl_active );
    }
    barrier(CLK_LOCAL_MEM_FENCE);
}


void extract_invalidate_inactive_l( LCriticalPoint * lcps, int lcl_idx, int cnt ){
    if( lcl_idx < cnt && (lcps+lcl_idx)->setN <= 1 ){
    	(lcps+lcl_idx)->value = -FLT_MAX;
    	(lcps+lcl_idx)->setN  = -1;
    }
    barrier(CLK_LOCAL_MEM_FENCE);
}







void clear_locks( int lcl_idx, int groupSize, __global int * d_scratch, int imgSize );
void clear_locks( int lcl_idx, int groupSize, __global int * d_scratch, int imgSize ) {
	for( int i = lcl_idx; i < imgSize; i+=groupSize){
		d_scratch[i] = INT_MAX;
	}
	barrier(CLK_GLOBAL_MEM_FENCE);

}

void set_locks( LCriticalPoint * curr, int currIdx, __global int * d_scratch );
void set_locks( LCriticalPoint * curr, int currIdx, __global int * d_scratch ) {
	for(int i = 0; i < curr->setN; i++ ){
		if( curr->setID[i] >= 0 )
			atomic_min( (d_scratch+curr->setID[i]), currIdx );
	}
}

void set_locks_g( GCriticalPoint * curr, int currIdx, __global int * d_scratch );
void set_locks_g( GCriticalPoint * curr, int currIdx, __global int * d_scratch ) {
	for(int i = 0; i < curr->ref; i++ ){
		if( curr->setID[i] >= 0 )
			atomic_min( (d_scratch+curr->setID[i]), currIdx );
	}
}

bool acquire_lock( LCriticalPoint * curr, int currIdx, __global int * d_scratch );
bool acquire_lock( LCriticalPoint * curr, int currIdx, __global int * d_scratch ) {
	bool acq = (curr->setN >= 2);
	for(int i = 0; i < curr->setN; i++ ){
		acq = acq && (*(d_scratch+curr->setID[i]) == currIdx );
	}
	return acq;
}




void kernel_cps_propagate_merges( LCriticalPoint * lcps, __local int * _masterIdx, __global uint * d_djs, __global int * d_cps, __global int * d_scratch, int imageW, int imageH, int groupSize );
void kernel_cps_propagate_merges( LCriticalPoint * lcps, __local int * _masterIdx, __global uint * d_djs, __global int * d_cps, __global int * d_scratch, int imageW, int imageH, int groupSize ){

	int thrdIdx = get_global_id(0);
	//int locW    = get_local_size(0);

	int cnt = ((*d_cps)-1)/(sizeof(GCriticalPoint)/4);

	GCriticalPoint* write_cps = (GCriticalPoint*)(d_cps+1);
	GCriticalPoint* read_cps  = (write_cps+cnt);
	LCriticalPoint * curr = lcps+thrdIdx;

	__global int * min_proc = d_scratch+imageW*imageH;
	__global int * total_iter = d_scratch+imageW*imageH+1;
	*min_proc = 0;
	*total_iter = 0;

	int curIdx = atomic_inc( _masterIdx );

    if( curIdx < cnt ){
    	move_cp_g2l( read_cps+curIdx, lcps+thrdIdx );
    }

    for( int loop = 1; loop <= 1000; loop++ ){
    	if( thrdIdx == 0 ) *total_iter = loop;
    	//__global int * proc_counter = d_scratch+imageW*imageH+2+(loop-1)*2;

		clear_locks( thrdIdx, groupSize, d_scratch, imageW*imageH );
		if( curIdx < cnt ) set_locks( curr, curIdx, d_scratch );
		barrier(CLK_GLOBAL_MEM_FENCE);


		if( curIdx < cnt ){
			setDJS( d_djs, curr->location, curr->location );
			if( curr->setN >= 2 ){
				for(int i = 0; i < curr->setN; i++ ){
					if( *(d_scratch+curr->setID[i]) == curIdx ){
						setDJS( d_djs, curr->setID[i], curr->location );
						curr->setID[i] = -curr->setID[i];
					}
				}
			}
		}
		barrier(CLK_GLOBAL_MEM_FENCE);

		if( curIdx < cnt && curr->setN >= 2 ){
			int newref = 0;
			for(int i = 0; i < curr->setN; i++ ){
				if( curr->setID[i] >= 0 ){
					curr->setID[i] = findDJS( d_djs, curr->setID[i] );
					newref++;
				}
			}
			if( newref <= 1 ){
				curr->setN = -curr->setN;
			}
			else{
				set_fix_l( curr );
			}
		}
		barrier(CLK_GLOBAL_MEM_FENCE);



		// export results and grab the next available nodes
		if( curIdx < cnt && curr->setN < 2 ){
			move_cp_l2g( lcps+thrdIdx, write_cps+curIdx );
			curIdx = atomic_inc( _masterIdx );
			if( curIdx < cnt )
				move_cp_g2l( read_cps+curIdx, lcps+thrdIdx );
		}

    }


}


__kernel void
kernel_cps_propagate_merges_256( int imageW, int imageH,
					__global uint * d_djs,
					__global int * d_cps,
					int groupSize,
					__global int * d_scratch
         	 	 	   )
{

    LCriticalPoint lcps[256];

    __local int masterIdx;
    masterIdx = 0;

    kernel_cps_propagate_merges( lcps, &masterIdx, d_djs, d_cps, d_scratch, imageW, imageH, groupSize );

}






__kernel void
kernel_cps_propagate_merges_1024( int imageW, int imageH,
					__global uint * d_djs,
					__global int * d_cps,
					int groupSize,
					__global int * d_scratch
         	 	 	   )
{

    LCriticalPoint lcps[1024];

    __local int masterIdx;
    masterIdx = 0;

    kernel_cps_propagate_merges( lcps, &masterIdx, d_djs, d_cps, d_scratch, imageW, imageH, groupSize );

}











/*

void hash_init_locks( LHashTable * locks, LCriticalPoint* curr );
void hash_init_locks( LHashTable * locks, LCriticalPoint* curr ){
	for(int i = 0; i < curr->setN; i++ ){
		if( curr->setID[i] >= 0 ){
			hash_put_l( locks, curr->setID[i], INT_MAX );
		}
	}
}

void hash_set_locks( LHashTable * locks, LCriticalPoint* cp, int cpIdx );
void hash_set_locks( LHashTable * locks, LCriticalPoint* cp, int cpIdx ){
	if( cp->setN >= 2 ){
		for(int i = 0; i < cp->setN; i++ ){
			if( cp->setID[i] >= 0 ){
				hash_atomic_min( locks, cp->setID[i], cpIdx );
			}
		}
	}
}


bool hash_acquire_lock( LHashTable * locks, LCriticalPoint * cp, int cpIdx );
bool hash_acquire_lock( LHashTable * locks, LCriticalPoint * cp, int cpIdx ) {
	bool acq = (cp->setN >= 2);
	for(int i = 0; i < cp->setN; i++ ){
		if( cp->setID[i] >= 0 ){
			acq = acq && ( hash_get_l( locks, cp->setID[i] ) == cpIdx );
		}
	}
	return acq;
}


void kernel_cps_propagate_merges_phase_1( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch );
void kernel_cps_propagate_merges_phase_1( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch ){


	int gThrdIdx = get_global_id(0);
	int gThrdW   = get_global_size(0);
	int group	 = get_group_id(0);
	int lThrdIdx = get_local_id(0);
	int lThrdW   = get_local_size(0);


	int cpsGroups = (cpsN+lThrdW-1)/lThrdW;

	if( group >= cpsGroups ) return;


	//GCriticalPoint* curr = read_cps+gThrdIdx;
	GCriticalPoint* gCurr = read_cps+gThrdIdx;
	LCriticalPoint* lCurr = lcps+lThrdIdx;

	if( gThrdIdx < cpsN ){
		move_cp_g2l( gCurr, lCurr );
	}


	GHashTable * hdjs = ((GHashTable*)d_scratch)+group;
	hash_init_g( hdjs, lThrdIdx );
	barrier(CLK_GLOBAL_MEM_FENCE);


	for( int loop = 0; loop < 150; loop++ ){
		hash_init_l( hashtable, lThrdIdx );
		barrier(CLK_LOCAL_MEM_FENCE);

		if( gThrdIdx < cpsN ) { hash_init_locks( hashtable, lCurr ); }
		barrier(CLK_LOCAL_MEM_FENCE);

		if( gThrdIdx < cpsN ) {
			hash_set_locks( hashtable, lCurr, gThrdIdx );
		}
		barrier(CLK_LOCAL_MEM_FENCE);

		if( gThrdIdx < cpsN ){
			if( hash_acquire_lock( hashtable, lCurr, gThrdIdx ) ){
				for(int i = 0; i < lCurr->setN; i++){
					hash_set_g( hdjs, lCurr->setID[i], lCurr->location );
				}
				lCurr->setN = -lCurr->setN;
			}
		}
		barrier(CLK_GLOBAL_MEM_FENCE);

		if( gThrdIdx < cpsN ){
			if( lCurr->setN >= 2 ){
				for(int i = 0; i < lCurr->setN; i++){
					int oldSID = lCurr->setID[i];
					lCurr->setID[i] = hash_djs_find_g( hdjs, oldSID );
					hash_set_if_exists_g( hdjs, oldSID, lCurr->setID[i] );
				}
				set_fix_l( lCurr );
			}
		}
		barrier(CLK_GLOBAL_MEM_FENCE);

	}

	//move_cp_l2g( lCurr, gCurr );


}

void kernel_cps_propagate_merges_phase_3( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch );
void kernel_cps_propagate_merges_phase_3( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch ){


	//return ;

	int gThrdIdx = get_global_id(0);
	//int gThrdW   = get_global_size(0);
	int group	 = get_group_id(0);
	int lThrdIdx = get_local_id(0);


	//GCriticalPoint* curr = read_cps+gThrdIdx;
	GCriticalPoint* gCurr = read_cps+gThrdIdx;
	LCriticalPoint* lCurr = lcps+lThrdIdx;

	if( gThrdIdx >= cpsN ) return;

	move_cp_g2l( gCurr, lCurr );

	for( int cgroup = group-1; cgroup >= 0; cgroup-- ){
	//for( int cgroup = 0; cgroup < group; cgroup++ ){
		GHashTable * hdjs = ((GHashTable*)d_scratch)+cgroup;

		if( lCurr->setN >= 2 ){
			for(int i = 0; i < lCurr->setN; i++){
				lCurr->setID[i] = hash_djs_find_g( hdjs, lCurr->setID[i] );
			}
			set_fix_l( lCurr );
		}
	}

	move_cp_l2g( lCurr, gCurr );



}


void kernel_cps_propagate_merges_phase_4( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch );
void kernel_cps_propagate_merges_phase_4( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch ){

	//return;

	int gThrdIdx = get_global_id(0);
	//int gThrdW   = get_global_size(0);
	int group	 = get_group_id(0);
	int lThrdIdx = get_local_id(0);


	//GCriticalPoint* curr = read_cps+gThrdIdx;
	GCriticalPoint* gCurr = read_cps+gThrdIdx;
	LCriticalPoint* lCurr = lcps+lThrdIdx;

	move_cp_g2l( gCurr, lCurr );

	GHashTable * hdjs = ((GHashTable*)d_scratch)+group;

	hash_init_g( hdjs, lThrdIdx );
	barrier(CLK_GLOBAL_MEM_FENCE);

	for( int loop = 0; loop < 10; loop++ ){
		hash_init_l( hashtable, lThrdIdx );
		barrier(CLK_LOCAL_MEM_FENCE);

		if( gThrdIdx < cpsN ) { hash_init_locks( hashtable, lCurr ); }
		barrier(CLK_LOCAL_MEM_FENCE);

		if( gThrdIdx < cpsN ) {
			hash_set_locks( hashtable, lCurr, gThrdIdx );
		}
		barrier(CLK_LOCAL_MEM_FENCE);

		if( gThrdIdx < cpsN ){
			if( hash_acquire_lock( hashtable, lCurr, gThrdIdx ) ){
				for(int i = 0; i < lCurr->setN; i++){
					hash_set_g( hdjs, lCurr->setID[i], lCurr->location );
				}
				lCurr->setN = -lCurr->setN;
			}
		}
		barrier(CLK_GLOBAL_MEM_FENCE);

		if( gThrdIdx < cpsN ){
			if( lCurr->setN >= 2 ){
				for(int i = 0; i < lCurr->setN; i++){
					int oldSID = lCurr->setID[i];
					lCurr->setID[i] = hash_djs_find_g( hdjs, oldSID );
					hash_set_if_exists_g( hdjs, oldSID, lCurr->setID[i] );
				}
				set_fix_l( lCurr );
			}
		}
		barrier(CLK_GLOBAL_MEM_FENCE);

	}

	move_cp_l2g( lCurr, gCurr );
}






__kernel void
kernel_cps_propagate_merges_phased( int imageSize,
					__global uint * d_djs,
					__global int  * d_cps,
					__global int * d_scratch,
					int phase
         	 	 	   )
{

    LCriticalPoint lcps[256];
    LHashTable hashtable;

    __local int masterIdx;
    if( get_local_id(0) == 0 )
    	masterIdx = 0;

	int cpsN = ((*d_cps)-1)/(sizeof(GCriticalPoint)/4);

	GCriticalPoint* write_cps = (GCriticalPoint*)(d_cps+1);
	//GCriticalPoint* read_cps  = (write_cps+cpsN);
	//GCriticalPoint* curr      = read_cps+gThrdIdx;

    //if( phase == 1 ) kernel_cps_propagate_merges_phase_1( d_scratch, imageSize );
    if( phase == 2 ) kernel_cps_propagate_merges_phase_1( lcps, &hashtable, write_cps, cpsN, d_djs, d_scratch );
    if( phase == 3 ) kernel_cps_propagate_merges_phase_3( lcps, &hashtable, write_cps, cpsN, d_djs, d_scratch );
    if( phase == 4 ) kernel_cps_propagate_merges_phase_4( lcps, &hashtable, write_cps, cpsN, d_djs, d_scratch );

}
*/




void hash_init_locks( LHashTable * locks, LCriticalPoint* curr );
void hash_init_locks( LHashTable * locks, LCriticalPoint* curr ){
	for(int i = 0; i < curr->setN; i++ ){
		if( curr->setID[i] >= 0 ){
			hash_put_l( locks, curr->setID[i], INT_MAX );
		}
	}
}

void hash_set_locks( LHashTable * locks, LCriticalPoint* cp, int cpIdx );
void hash_set_locks( LHashTable * locks, LCriticalPoint* cp, int cpIdx ){
	if( cp->setN >= 2 ){
		for(int i = 0; i < cp->setN; i++ ){
			if( cp->setID[i] >= 0 ){
				hash_atomic_min( locks, cp->setID[i], cpIdx );
			}
		}
	}
}


bool hash_acquire_lock( LHashTable * locks, LCriticalPoint * cp, int cpIdx );
bool hash_acquire_lock( LHashTable * locks, LCriticalPoint * cp, int cpIdx ) {
	bool acq = (cp->setN >= 2);
	for(int i = 0; i < cp->setN; i++ ){
		if( cp->setID[i] >= 0 ){
			acq = acq && ( hash_get_l( locks, cp->setID[i] ) == cpIdx );
		}
	}
	return acq;
}


void kernel_cps_propagate_merges_phase_local( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch );
void kernel_cps_propagate_merges_phase_local( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch ){


	int gThrdIdx = get_global_id(0);
	int gThrdW   = get_global_size(0);
	int group	 = get_group_id(0);
	int lThrdIdx = get_local_id(0);
	int lThrdW   = get_local_size(0);


	int cpsGroups = (cpsN+lThrdW-1)/lThrdW;

	if( group >= cpsGroups ) return;


	//GCriticalPoint* curr = read_cps+gThrdIdx;
	GCriticalPoint* gCurr = read_cps+gThrdIdx;
	LCriticalPoint* lCurr = lcps+lThrdIdx;

	if( gThrdIdx < cpsN ){
		move_cp_g2l( gCurr, lCurr );
	}


	GHashTable * hdjs = ((GHashTable*)d_scratch)+group;
	hash_init_g( hdjs, lThrdIdx );
	barrier(CLK_GLOBAL_MEM_FENCE);

	hash_init_l( hashtable, lThrdIdx );
	barrier(CLK_LOCAL_MEM_FENCE);

	if( gThrdIdx < cpsN ) { hash_init_locks( hashtable, lCurr ); }
	barrier(CLK_LOCAL_MEM_FENCE);

	if( gThrdIdx < cpsN ) {
		hash_set_locks( hashtable, lCurr, gThrdIdx );
	}
	barrier(CLK_LOCAL_MEM_FENCE);

	if( gThrdIdx < cpsN ){
		int liveCount = 0;
		for(int i = 0; i < lCurr->setN; i++ ){
			if( lCurr->setID[i] >= 0 ){
				int owner = hash_get_l( hashtable, lCurr->setID[i] );
				if( gThrdIdx != owner ){
					lCurr->setID[i] = (lcps+owner)->location;
					liveCount++;
				}
				else{
					lCurr->setID[i] = -lCurr->setID[i];
				}
			}
		}
		if( liveCount <= 1 ){
			lCurr->setN = -abs(lCurr->setN);
		}
	}
	barrier(CLK_LOCAL_MEM_FENCE);

	for( int loop = 0; loop < 50; loop++){
		if( gThrdIdx < cpsN ) { hash_init_locks( hashtable, lCurr ); }
		barrier(CLK_LOCAL_MEM_FENCE);

		if( gThrdIdx < cpsN ) {
			hash_set_locks( hashtable, lCurr, gThrdIdx );
		}
		barrier(CLK_LOCAL_MEM_FENCE);


		if( gThrdIdx < cpsN ) {
			for(int i = 0; i < abs(lCurr->setN); i++ ){
				if( lCurr->setID[i] >= 0 ){
					int owner = hash_get_l( hashtable, lCurr->setID[i] );
					if( gThrdIdx > owner && hash_acquire_lock( hashtable, lcps+owner, owner ) ){
						//lCurr->setID[i] = (lcps+owner)->location;
						lCurr->setID[i] = (lcps+owner)->setID[0];
					}
				}
			}
		}
		barrier(CLK_LOCAL_MEM_FENCE);

		if( gThrdIdx < cpsN ) {
			if( hash_acquire_lock( hashtable, lCurr, gThrdIdx ) ){
				lCurr->setN = -lCurr->setN;
			}
		}
		barrier(CLK_LOCAL_MEM_FENCE);
	}


	if( gThrdIdx < cpsN ) {
		for(int i = 0; i < abs(lCurr->setN) && i < 4; i++ ){
			lCurr->setID[i] = abs(lCurr->setID[i]);
		}
		lCurr->setN = abs(lCurr->setN);
	}
	set_fix_l( lCurr );

		move_cp_l2g( lCurr, gCurr );


}


void kernel_cps_propagate_merges_phase_1( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch );
void kernel_cps_propagate_merges_phase_1( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch ){


	int gThrdIdx = get_global_id(0);

	if( gThrdIdx < cpsN ){
		d_scratch[gThrdIdx] = INT_MAX;
	}
}


void kernel_cps_propagate_merges_phase_2( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch );
void kernel_cps_propagate_merges_phase_2( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch ){

	int gThrdIdx = get_global_id(0);

	GCriticalPoint* gCurr = read_cps+gThrdIdx;

	if( gThrdIdx < cpsN ){
		for(int i = 0; i < gCurr->ref; i++ ){
			if( gCurr->setID[i] >= 0 ){
				atomic_min( &(d_scratch[gCurr->setID[i]]), gThrdIdx );
			}
		}
	}

}







typedef struct tag_hashdjsrecord {
	int key;
	int value;
	float data;
	int next;
} HashDJSRecord ;

#define DJS_BINS 2048
typedef __global struct tag_hashdjstable {
	int heappnt;
	int sets[DJS_BINS];
	HashDJSRecord records[16]; // this variable actually extends "infinitely"
} HashDJSTable;


void hash_djs_init( HashDJSTable * hash_table, int thrdIdx );
void hash_djs_init( HashDJSTable * hash_table, int thrdIdx ){
	if( thrdIdx == 0 ){
		hash_table->heappnt = 0;
	}
	if( thrdIdx < DJS_BINS ){
		hash_table->sets[thrdIdx]  = -1;
	}
}


bool hash_djs_get( HashDJSTable * hash_table, int key, int * value, float * data );
bool hash_djs_get( HashDJSTable * hash_table, int key, int * value, float * data ){
	int set = abs(key)%DJS_BINS;
	int cur = hash_table->sets[set];

	while( cur >= 0 ){
		if( key == hash_table->records[cur].key ){
			*data  = hash_table->records[cur].data;
			*value = hash_table->records[cur].value;
			return true;
		}
		cur = hash_table->records[cur].next;
	}
	return false;
}

void hash_djs_put( HashDJSTable * hash_table, int key, int value, int data );
void hash_djs_put( HashDJSTable * hash_table, int key, int value, int data ){
	int set = abs(key)%DJS_BINS;
	int offset = atomic_inc( &(hash_table->heappnt) );

	hash_table->records[offset].value = value;
	hash_table->records[offset].data  = data;
	hash_table->records[offset].key   = key;
	hash_table->records[offset].next  = atomic_xchg( &(hash_table->sets[set]), offset );
}




bool propagate_l( LCriticalPoint * lcps, int lThrdIdx, LHashTable * hashtable, __local int * working_counter );
bool propagate_l( LCriticalPoint * lcps, int lThrdIdx, LHashTable * hashtable, __local int * working_counter ){

	LCriticalPoint * lCurr = lcps + lThrdIdx;
	bool active = lCurr->setN >= 2;

	if( active ){
		atomic_inc( working_counter );
	}
	hash_init_l( hashtable, lThrdIdx );
	barrier(CLK_LOCAL_MEM_FENCE);

	// init locks
	if( active ){
		for(int i = 0; i < lCurr->setN; i++ ){
			if( lCurr->setID[i] >= 0 ){
				hash_put_l( hashtable, lCurr->setID[i], INT_MAX );
			}
		}
	}
	barrier(CLK_LOCAL_MEM_FENCE);

	for( int loop = 0; loop < 255 && working_counter > 0; loop++ ){

		// set locks
		if( active ){
			for(int i = 1; i < lCurr->setN; i++ ){
				if( lCurr->setID[i] >= 0 ){
					hash_atomic_min( hashtable, lCurr->setID[i], lThrdIdx );
				}
			}
		}
		barrier(CLK_LOCAL_MEM_FENCE);

		if( active ){
			bool hasLock = true;
			for( int i = 1; i < lCurr->setN; i++ ){
				hasLock = hasLock && ( hash_get_l( hashtable, lCurr->setID[i] ) == lThrdIdx );
			}

			if( hasLock ){
				atomic_dec( working_counter );
				active = false;
			}
			else{
				for(int i = 0; i < lCurr->setN; i++ ){
					int owner;
					do {
						owner = hash_get_l( hashtable, lCurr->setID[i] );
						if( owner != INT_MAX ){
							lCurr->setID[i] = (lcps+owner)->setID[0];
						}
					} while( owner != INT_MAX );
				}
				set_fix_l( lCurr );
				if( lCurr->setN <= 1 ){
					atomic_dec( working_counter );
					active = false;
				}
			}
		}
		barrier(CLK_LOCAL_MEM_FENCE);
	}
	return lCurr->setN >= 2;
}





void kernel_cps_propagate_merges_phase_3( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch, __local int * proc_count, GCriticalPoint* tmp_cps, __local int * localcounter, __local int * working_counter );
void kernel_cps_propagate_merges_phase_3( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch, __local int * proc_count, GCriticalPoint* tmp_cps, __local int * localcounter, __local int * working_counter ){


	int gThrdIdx = get_global_id(0);
	int gThrdW   = get_global_size(0);
	int group	 = get_group_id(0);
	int lThrdIdx = get_local_id(0);
	int lThrdW   = get_local_size(0);


	if( group == 0 ){
		HashDJSTable * djshash = (HashDJSTable*)d_djs;
		for(int i = lThrdIdx; i < 2048; i+=lThrdW ){
			hash_djs_init( djshash, i );
		}
	}

	int cpsGroups = (cpsN+lThrdW-1)/lThrdW;

	if( group >= cpsGroups ) return;

	GCriticalPoint* gCurr = read_cps+gThrdIdx;
	LCriticalPoint* lCurr = lcps+lThrdIdx;

	cp_invalidate_g( tmp_cps+gThrdIdx );

	localcounter[lThrdIdx] = 0;

	bool export = false;
	if( gThrdIdx < cpsN ){
		int liveCount = 0;
		for(int i = 0; i < gCurr->ref; i++ ){
			if( gCurr->setID[i] >= 0 ){
				int owner = d_scratch[gCurr->setID[i]];
				if( gThrdIdx != owner ){
					gCurr->setID[i] = (read_cps+owner)->location;
					liveCount++;
				}
				else{
					//gCurr->setID[i] = -gCurr->setID[i];
				}
			}
		}
		set_fix_g( gCurr );
		localcounter[lThrdIdx] = ( gCurr->ref > 1 && liveCount > 1 ) ? 1 : 0;
	}
	barrier(CLK_GLOBAL_MEM_FENCE);

	cp_invalidate_l( lCurr );
	if( localcounter[lThrdIdx] == 1 ){
		move_cp_g2l( gCurr, lCurr );
	}

/*
	if( propagate_l(lcps, lThrdIdx, hashtable, working_counter) ){
		move_cp_l2g( lCurr, tmp_cps+gThrdIdx );
	}
*/

	if( localcounter[lThrdIdx] == 1 ){
		move_cp_l2g( lCurr, tmp_cps+gThrdIdx );
	}


	barrier(CLK_GLOBAL_MEM_FENCE);


}




void kernel_cps_propagate_merges_phase_2( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch, __local int * proc_count, GCriticalPoint* tmp_cps, __local int * localcounter, __local int * working_counter  );
void kernel_cps_propagate_merges_phase_2( LCriticalPoint * lcps, LHashTable * hashtable, GCriticalPoint* read_cps, int cpsN, __global uint * d_djs, __global int * d_scratch, __local int * proc_count, GCriticalPoint* tmp_cps, __local int * localcounter, __local int * working_counter  ){

	int gThrdIdx = get_global_id(0);
	int gThrdW   = get_global_size(0);
	int group	 = get_group_id(0);
	int lThrdIdx = get_local_id(0);
	int lThrdW   = get_local_size(0);

	LCriticalPoint * lCurr = lcps+lThrdIdx;

	int cpsGroups = (cpsN+lThrdW-1)/lThrdW;

	if( group >= cpsGroups ) return;

	/*
	if( gThrdIdx < cpsN ){
		move_cp_g2l( tmp_cps+gThrdIdx, lCurr );
	}
	else{
		cp_invalidate_l( lCurr );
	}

	for( int loop = 0; loop < 10; loop++ ){
		for(int i = 1; i < lCurr->setN; i++ ){
			atomic_min( &(d_scratch[lCurr->setID[i]]), gThrdIdx );
		}
		barrier(CLK_GLOBAL_MEM_FENCE);

		for(int i = 0; i < lCurr->setN; i++ ){
			int owner = d_scratch[lCurr->setID[i]];
			if( owner < gThrdIdx ){
				lCurr->setID[i] = (read_cps+owner)->setID[0];
				owner = d_scratch[lCurr->setID[i]];
				//atomic_min( &(d_scratch[lCurr->setID[i]]), gThrdIdx );
			}
		}
		set_fix_l( lCurr );
		barrier(CLK_GLOBAL_MEM_FENCE);

		if( lCurr->setN >= 1 ){
			if( lCurr->setN == 1 ) cp_invalidate_l( lCurr );
			move_cp_l2g( lCurr, tmp_cps+gThrdIdx );
		}
	}
	barrier(CLK_GLOBAL_MEM_FENCE);
	*/


	cp_invalidate_l( lCurr );

	bool modified = false;

	if( gThrdIdx < cpsN ){
		move_cp_g2l( read_cps+gThrdIdx, lCurr );

		for(int loop = 0; loop < 10; loop++ ){
			for(int i = 0; i < lCurr->setN; i++ ){
				int owner = d_scratch[lCurr->setID[i]];
				if( owner < gThrdIdx ){
					lCurr->setID[i] = (tmp_cps+owner)->setID[0];
					modified = true;
				}
			}
		}
		set_fix_l( lCurr );
	}
	barrier(CLK_GLOBAL_MEM_FENCE);

	if( modified ){
		if( lCurr->setN == 1 ) cp_invalidate_l( lCurr );
		move_cp_l2g( lCurr, read_cps+gThrdIdx );
		for(int i = 1; i < lCurr->setN; i++ ){
			atomic_min( &(d_scratch[lCurr->setID[i]]), gThrdIdx );
		}
	}

}




int merge_cps_subsets( int nGrp, int grpOffset, __global int * d_cps, int x, GCriticalPoint* cps, int cnt );
int merge_cps_subsets( int nGrp, int grpOffset, __global int * d_cps, int x, GCriticalPoint* cps, int cnt ){
	for( int grp = 1; grp < nGrp; grp++){

		int heap_off1 = grp*grpOffset;

		//Heap heap_cps1 = hload( d_cps+heap_off1 );

		GCriticalPoint* cps1 = (GCriticalPoint*)(d_cps+heap_off1+1);

		int cnt1 = ((*(d_cps+heap_off1))-1)/(sizeof(GCriticalPoint)/4);

		if( x < cnt1 ){
			swap_cp_g( cps+cnt+x, cps1+x );
		}

		barrier(CLK_GLOBAL_MEM_FENCE);

		cnt += cnt1;
		if( x == 0 ){
			(*d_cps)+=cnt1*(sizeof(GCriticalPoint)/4);
		}
	}

	barrier(CLK_GLOBAL_MEM_FENCE);

	return cnt;

}


/*

void bitonic_sorter_by_ref_first_pass( GCriticalPoint* mil, int idx, int phase, int listsize );

void bitonic_sorter_by_ref_first_pass( GCriticalPoint* mil, int idx, int phase, int listsize ){

	int offset = (idx%(1<<phase));
	int thisGrp = (idx-offset)*2;
	int nextGrp = thisGrp + (2<<phase);

	int idx0 = thisGrp + offset;
	int idx1 = nextGrp - offset - 1;

	if( idx1 < listsize ){
		GCriticalPoint* m0 = (mil+idx0);
		GCriticalPoint* m1 = (mil+idx1);
		bool c0 = m0->ref >= 2;
		bool c1 = m1->ref >= 2;
		if( c0 && !c1 ){
			swap_cp_g( m0, m1 );
		}
		if( c0 == c1 && m0->value <  m1->value ){
			swap_cp_g( m0, m1 );
		}
	}

}

void bitonic_sorter_by_ref( GCriticalPoint* mil, int idx, int phase, int listsize );

void bitonic_sorter_by_ref( GCriticalPoint* mil, int idx, int phase, int listsize ){

	int offset = (idx%(1<<phase));
	int thisGrp = (idx-offset)*2;

	int idx0 = thisGrp + offset;
	int idx1 = thisGrp + offset + (1<<phase);

	if( idx1 < listsize ){
		GCriticalPoint* m0 = (mil+idx0);
		GCriticalPoint* m1 = (mil+idx1);
		bool c0 = m0->ref >= 2;
		bool c1 = m1->ref >= 2;
		if( c0 && !c1 ){
			swap_cp_g( m0, m1 );
		}
		if( c0 == c1 && m0->value <  m1->value ){
			swap_cp_g( m0, m1 );
		}

	}
}

void sort_cps_by_ref( int idx, GCriticalPoint* mil, int listsize );

void sort_cps_by_ref( int idx, GCriticalPoint* mil, int listsize ){
	int iter = ilog2(listsize)+1;

	for( int i = 0; i < iter; i++ ){
		bitonic_sorter_by_ref_first_pass( mil, idx, i, listsize );
		barrier(CLK_GLOBAL_MEM_FENCE);

		for(int j = i-1; j >= 0; j-- ){
			bitonic_sorter_by_ref( mil, idx, j, listsize );
			barrier(CLK_GLOBAL_MEM_FENCE);
		}
	}

}

*/



/*
void wg_sort_cps( int idx, GCriticalPoint* mil, int listsize );

void wg_sort_cps( int idx, GCriticalPoint* mil, int listsize ){
	int iter = ilog2(listsize)+1;

	for( int i = 0; i < iter; i++ ){
		bitonic_sorter_first_pass( mil, idx, i, listsize );
		barrier(CLK_GLOBAL_MEM_FENCE);

		for(int j = i-1; j >= 0; j-- ){
			bitonic_sorter( mil, idx, j, listsize );
			barrier(CLK_GLOBAL_MEM_FENCE);
		}
	}

}
*/



/*

void bitonic_sorter_first_pass( __global void* data, int idx, int phase, int listsize );

void bitonic_sorter_first_pass( __global void* data, int idx, int phase, int listsize ){

	int offset  = (idx%(1<<phase));
	int thisGrp = (idx-offset)*2;
	int nextGrp = thisGrp + (2<<phase);

	int idx0 = thisGrp + offset;
	int idx1 = nextGrp - offset - 1;

	if( idx1 < listsize ){
		compare_and_swap_g( data, idx0, idx1 );
	}

}

void bitonic_sorter( __global void* data, int idx, int phase, int listsize );

void bitonic_sorter( __global void* data, int idx, int phase, int listsize ){

	int offset  = (idx%(1<<phase));
	int thisGrp = (idx-offset)*2;

	int idx0 = thisGrp + offset;
	int idx1 = thisGrp + offset + (1<<phase);

	if( idx1 < listsize ){
		compare_and_swap_g( data, idx0, idx1 );
	}
}
*/


/*
void sort_cps( int idx, GCriticalPoint* mil, int listsize, int thrdW );

void sort_cps( int idx, GCriticalPoint* mil, int listsize, int thrdW ){
	int iter = ilog2(listsize)+2;

	for( int i = 0; i < iter; i++ ){

		for(int curIdx = idx; curIdx < listsize; curIdx+=thrdW )
			bitonic_sorter_first_pass( mil, curIdx, i, listsize );
		barrier(CLK_GLOBAL_MEM_FENCE);

		for(int j = i-1; j >= 0; j-- ){

			for(int curIdx = idx; curIdx < listsize; curIdx+=thrdW )
				bitonic_sorter( mil, curIdx, j, listsize );
			barrier(CLK_GLOBAL_MEM_FENCE);

		}
	}

}*/


/*

void bitonic_sorter_first_pass_l( __local void* data, int idx, int phase, int listsize );

void bitonic_sorter_first_pass_l( __local void* data, int idx, int phase, int listsize ){

	int offset = (idx%(1<<phase));
	int thisGrp = (idx-offset)*2;
	int nextGrp = thisGrp + (2<<phase);

	int idx0 = thisGrp + offset;
	int idx1 = nextGrp - offset - 1;

	if( idx1 < listsize ){
		compare_and_swap_l( data, idx0, idx1 );
	}

}

void bitonic_sorter_l( __local void* data, int idx, int phase, int listsize );

void bitonic_sorter_l( __local void* data, int idx, int phase, int listsize ){

	int offset = (idx%(1<<phase));
	int thisGrp = (idx-offset)*2;

	int idx0 = thisGrp + offset;
	int idx1 = thisGrp + offset + (1<<phase);

	if( idx1 < listsize ){
		compare_and_swap_l( data, idx0, idx1 );
	}

}
*/



/*
void sort_cps_l( __local void* data, int idx, int listsize );

void sort_cps_l( __local void* data, int idx, int listsize ){
	int iter = ilog2(listsize)+2;

	for( int i = 0; i < iter; i++ ){
		bitonic_sorter_first_pass_l( data, idx, i, listsize );
		barrier(CLK_LOCAL_MEM_FENCE);

		for(int j = i-1; j >= 0; j-- ){
			bitonic_sorter_l( data, idx, j, listsize );
			barrier(CLK_LOCAL_MEM_FENCE);
		}
	}

}
*/



void quick_remove_edges_g( GCriticalPoint * cps, int cnt, int lcl_idx );
void quick_remove_edges_g( GCriticalPoint * cps, int cnt, int lcl_idx ){
    for(int i = 0; i < cnt; i++ ){
       	if( cps[i].ref >= 2 ){
       		for( int k = 1; k < cps[i].ref; k++ ){
       			int rplc = (cps+i)->setID[k];
       			int with = (cps+i)->setID[0];
       			int proc_idx = i+1 + lcl_idx;
   				if( proc_idx < cnt ){
   					for( int j = 0; j < (cps+proc_idx)->ref; j++ ){
   						if( (cps+proc_idx)->setID[j] == rplc ){
   							(cps+proc_idx)->setID[j] = with;
   						}
   					}
   					fixSets( (cps+proc_idx) );
   				}
   			}
       	}

   	    barrier(CLK_GLOBAL_MEM_FENCE);
    }
}


void invalidate_inactive_g( GCriticalPoint * cps, int lcl_idx, __local int * ttl_active );
void invalidate_inactive_g( GCriticalPoint * cps, int lcl_idx, __local int * ttl_active ){
    if( (cps+lcl_idx)->ref <= 1 ){
    	(cps+lcl_idx)->value = -FLT_MAX;
    	(cps+lcl_idx)->ref  = -1;
    	atomic_dec( ttl_active );
    }
    barrier(CLK_GLOBAL_MEM_FENCE);
}


int getSetOffsetByID( CriticalPoint * cp, int setID );
bool containsSetID( CriticalPoint * cp, int setID );
bool isSubset(  __global setList * set, __global setList * subset );
bool equalSets( __global setList * set0, __global setList * set1 );
void merge( __global int* d_heap, __global int* d_cps, int idx, int mul, int cnt, int data, int scratch );
void sortMaximaByID( __global int * d_heap, int idx, __global int* d_cps );
CriticalPoint * bsearchMaximaByID( __global int * d_heap, __global int * d_cps, int setID );
CriticalPoint * lsearchMaximaByID( __global int * d_heap, __global int * d_cps, int setID );
void binCriticalPoint( CriticalPoint * cp, __global int * d_heap, __global int * d_cps );
void buildTreeRemoveNonCP( CriticalPoint * curr, __global int * d_cps );
void buildTree( CriticalPoint * curr, __global int * d_cps, __global int * d_heap );
void buildTreeSaddles( CriticalPoint * curr, __global int * d_cps );



#if 0
	for( int loop = 0; loop < 100; loop++ ){

		for( int i = x; i < imageW*imageH; i+=groupSize){
			d_scratch[i] = INT_MAX;
		}
		barrier(CLK_GLOBAL_MEM_FENCE);

		for( int idx = x; idx <cnt; idx += groupSize ){
			GCriticalPoint * curr = cps+idx;
			if( curr->ref >= 2 ){
				updateSets( curr, d_djs );
				fixSets( curr );

				for(int i = 0; i < curr->ref; i++ ){
					if( curr->setID[i] >= 0 ){
						atomic_min( (d_scratch+curr->setID[i]), idx );
					}
				}
			}
		}
		barrier(CLK_GLOBAL_MEM_FENCE);

		for( int idx = x; idx <cnt; idx += groupSize ){
			GCriticalPoint * curr = cps+idx;
			if( curr->ref >= 2 ){

				bool proc = true;
				for(int i = 0; i < curr->ref; i++ ){
					if( curr->setID[i] >= 0 ){
						proc = proc && (*(d_scratch+curr->setID[i]) == idx);
					}
				}

				if( proc ){
					atomic_inc( d_scratch+imageW*imageH+2+loop*2+1 );
					curr->ref = -2;
					setDJS( d_djs, curr->location, curr->location );

					for(int i = 0; i < 8; i++){
						if( curr->setID[i] >= 0 ){
							setDJS( d_djs, curr->setID[i], curr->location );
						}
					}
				}
				/*
				else{
					atomic_min( min_proc, idx );
				}
				*/
			}
		}
		barrier(CLK_GLOBAL_MEM_FENCE);

	}
#elif 0


	for( int loop = 0; loop < cnt; loop++ ){

		if( *min_proc == cnt ) break;

		if( x == 0 ){
			(*total_iter)++;
			*(d_scratch+imageW*imageH+2+loop*2+0) = *min_proc;
		}

		int offset = *min_proc;
		int idx = offset+x;
		GCriticalPoint * curr = cps+idx;

		for( int i = x; i < imageW*imageH; i+=groupSize){
			d_scratch[i] = INT_MAX;
		}
		barrier(CLK_GLOBAL_MEM_FENCE);

		if( x == 0 ){
			*min_proc = min( cnt, idx+groupSize );
		}

		if( idx < cnt && curr->ref >= 2 ){
			updateSets( curr, d_djs );
			fixSets( curr );

			for(int i = 0; i < 8; i++ ){
				if( curr->setID[i] >= 0 ){
					atomic_min( (d_scratch+curr->setID[i]), idx );
				}
			}
		}
		barrier(CLK_GLOBAL_MEM_FENCE);

		if( idx < cnt && curr->ref >= 2 ){

			bool proc = true;
			for(int i = 0; i < 8; i++ ){
				if( curr->setID[i] >= 0 ){
					proc = proc && (*(d_scratch+curr->setID[i]) == idx);
				}
			}
			if( proc ){
				atomic_inc( d_scratch+imageW*imageH+2+loop*2+1 );
				curr->ref = -2;
				setDJS( d_djs, curr->location, curr->location );

				for(int i = 0; i < 8; i++){
					if( curr->setID[i] >= 0 ){
						setDJS( d_djs, curr->setID[i], curr->location );
					}
				}
			}
			else{
				atomic_min( min_proc, idx );
			}

		}
		barrier(CLK_GLOBAL_MEM_FENCE);

	}
#endif

#if 0

    extract_cps_g( x, y, imageW, imageH, d_input, d_djs, d_cps, heap_off );
    wg_sort_cps( lcl_idx, cps, cnt );
    quick_remove_edges_g( cps, cnt, lcl_idx );
    invalidate_inactive_g( cps, lcl_idx, &ttl_active );
    wg_sort_cps( lcl_idx, cps, cnt );

    if( lcl_idx == 0 ){
    	*(d_cps+heap_off) = 1 + ttl_active*sizeof(GCriticalPoint)/4;
    }
#endif

    // move the data from local memory to global memory
	#if 0
		move_cp_l2g( lcps+lcl_idx, cps+lcl_idx );

		if( lcl_idx == 0 ){
			*(d_cps+heap_off) = 1 + ttl_active*sizeof(GCriticalPoint)/4;
		}
	#else
		Heap cps_heap = hinit( d_cps );
		if( (lcps+lcl_idx)->setN >= 2 )
			move_cp_l2g( lcps+lcl_idx, (GCriticalPoint*)halloc( cps_heap, sizeof(GCriticalPoint)/4 ) );
	#endif


		void extract_cps_g( int x, int y, int imgW, int imgH, __global float * d_input, __global uint * d_djs, __global int * d_cps, int heap_off );
		void extract_cps_g( int x, int y, int imgW, int imgH, __global float * d_input, __global uint * d_djs, __global int * d_cps, int heap_off ){
		    Heap heap_cps = hinit( d_cps+heap_off );
		    if( validLocation(x,y,imgW,imgH) ){
				extractCriticalPoints( d_input, d_djs, x,y,imgW,imgH, heap_cps );
		    }
		    barrier(CLK_GLOBAL_MEM_FENCE);
		}





		GCriticalPoint * extractCriticalPoints( __global float * data, __global uint * djs, int u, int v, int w, int h, Heap heap_cps ); //, Heap heap_cp_list );

		GCriticalPoint * extractCriticalPoints( __global float * data, __global uint * djs, int u, int v, int w, int h, Heap heap_cps ){ //, Heap heap_cp_list ) {

			int  idx	  = getIndex( u,v,w,h );
		    int  cp_type  = getCriticalPointType( data,u,v,w,h );
			float myVal	  = getValue( data,u,v,w,h );

		    GCriticalPoint * newCP = 0;
		   	newCP = createInvalidCP( heap_cps, idx );

			if( cp_type == SADDLE ){
				int cnt = 0;
				for(int i = 0; i < 8; i++){
					int _u = u+neighborU[i];
					int _v = v+neighborV[i];
					if( validLocation( _u, _v, w, h ) ){
						float otherVal = getValue( data, _u,_v, w, h );
						if( otherVal >= myVal ){
							int sid = findDJS( djs, getIndex(_u,_v,w,h) );
							if( groupInsert( newCP->setID, sid ) ){
							cnt++;
							}
						}
					}
				}

				// true saddle
				if( cnt >= 2 ){
					newCP->value = myVal;
					newCP->ref   = cnt;
					//newCP->type  = cp_type;
				}

			}

			return newCP;
		}



		/*
		void hreset( Heap heap );

		void hreset( Heap heap ){
			*heap.base = 1;
		}


		Heap hsplit( Heap oldHeap );

		Heap hsplit( Heap oldHeap ){
			Heap ret;
			ret.base = oldHeap.base+(*oldHeap.base);
			*ret.base = 1;
			return ret;
		}


		__global void * hallocOffset( Heap heap, int size_ints, int * offset );

		__global void * hallocOffset( Heap heap, int size_ints, int * offset ){
		   	*offset = atomic_add( heap.base, size_ints );
		   	return (__global void*)(heap.base+*offset);
		}
		*/

	void updateSets_g( GCriticalPoint * curr, __global uint * d_djs );
	void updateSets_g( GCriticalPoint * curr, __global uint * d_djs ){
		for(int i = 0; i < curr->ref; i++){
			if( curr->setID[i] >= 0 ){
				curr->setID[i] = findDJS( d_djs, curr->setID[i] );
			}
		}
	}
	void selectLargestCP( int idx, GCriticalPoint* mil, int listsize );

	void selectLargestCP( int idx, GCriticalPoint* mil, int listsize ){
		int iter = ilog2(listsize)+1;
		for(int loop = 0; loop < iter; loop++){
			int idx1 = idx + (1<<loop);
			if( idx1 < listsize && (idx>>loop)%2 == 0 ){
				GCriticalPoint* m0 = (mil+idx);
				GCriticalPoint* m1 = (mil+idx1);
				if( m0->value <  m1->value ){
					swap_cp_g( m0, m1 );
				}
			}

			barrier(CLK_GLOBAL_MEM_FENCE);
		}

	}


#if 0
	{
		int grpOffset = 1+groupSize*(sizeof(GCriticalPoint)/4);
		int nGrp = (imageW*imageH)/groupSize;
		cnt = merge_cps_subsets( nGrp, grpOffset, d_cps, x, cps, cnt );
	}
#endif



/*

	int refcount = 0;
	int setsize  = 0;
   	int setTmp[8] = {-1,-1,-1,-1,-1,-1,-1,-1};

    if( cp_type != NORMAL ){
		for(int _v = v-1; _v <= v+1; _v++ ){
			for(int _u = u-1; _u <= u+1; _u++ ){
				if( validLocation( _u, _v, w, h ) ){
					float otherVal = getValue( data, _u,_v, w, h );

					if( otherVal >= myVal )
						setInsert( setTmp, findDJS(djs, getIndex(_u,_v,w,h)), 8 );
				}
			}
		}
		refcount = setSize( setTmp, 8 );
		setsize  = refcount;

		// If we only reference 1 set, not really a saddle point
		//if( cp_type == SADDLE && refcount <= 1 ) cp_type = NORMAL;
	}


    int cp_idx = 0;
    if( cp_type == NORMAL ){
    	newCP = createInvalidCP( heap_cps, idx );
    }
    else {
        newCP = createCP( heap_cps, idx, myVal, cp_type, refcount, setTmp );
    }

   return newCP;
*/

//CriticalPoint * createCP( Heap heap_cps, int idx, float value, int type, int refCount, int *sets );
//int getChildCount( CriticalPoint * cp );


/*
CriticalPoint * createCP( Heap heap_cps, int idx, float value, int type, int refCount, int *sets ){

	CriticalPoint * newCP = 0;
	newCP = (CriticalPoint *)halloc( heap_cps, sizeof(CriticalPoint)/4 ) ;

	newCP->location = idx;
	newCP->value    = value;
	newCP->ref      = refCount;
	newCP->type     = type;

	for(int i = 0; i < 8; i++){
		newCP->setID[i] = sets[i];
	}

	return newCP;
}
*/


/*

int getChildCount( CriticalPoint * cp ){
	int cnt = 0;
	for(int i = 0; i < 8; i++){
		if( cp->setID[i] >= 0 ) cnt++;
	}
	return cnt;
}
*/


/*
void setInsert( int * set, int val, int maxSize );

void setInsert( int * set, int val, int maxSize ){
	for(int i = 0; i < maxSize; i++){
		// value already exists, do nothing
		if( set[i] == val ){
			return;
		}
		// reached the end of the list, insert
		if( set[i] == -1 ){
			set[i] = val;
			return;
		}
		// this will sort points, basically bubble sort
		if( set[i] > val ){
			int tmp = set[i];
			set[i] = val;
			val = tmp;
		}
	}
}
*/
/*
int setSize( int * set, int maxSize );

int setSize( int * set, int maxSize ){
	for(int i = 0; i < maxSize; i++){
		if( set[i] == -1 ){
			return i;
		}
	}
	return maxSize;
}
*/


/*
typedef struct tag_setList {
	int setID;
	//int next;
} setList ;

typedef __global struct tag_CriticalPoint {
	int location;
	float value;
	int ref;
	int type;
	setList sets[8];
} CriticalPoint;
*/


typedef volatile __global struct tag_MergeItem {
	int location;
	float value;
	int set0;
	int set1;
} MergeItem;


void setInsert( int * set, int val, int maxSize );
uint setSize( int * set, int maxSize );

    /*
	sortMaximaByID( d_heap, idx, d_cps );
	binCriticalPoint( cp, d_heap, d_cps );
	orderLists( cp, d_heap, d_cps );
	*/
/*
	barrier(CLK_GLOBAL_MEM_FENCE);
	heap_gen = hinit( d_heap );
	if ( idx == 0 ) halloc( heap_gen, 1 );
	__global uint *counter = (d_heap+1);
	*counter = 0;
	//__global uint *counter = (__global uint*)halloc( heap_gen, 1 );
	//*counter = 0;        //initialize variable with zero
	barrier(CLK_GLOBAL_MEM_FENCE);

	if( cp != 0 ){
		for(int i = 0; i < 8; i++ ){
			if( cp->sets[i].setID < 0 ) continue;
			for(int j = i+1; j < 8; j++){
				if( cp->sets[j].setID < 0 ) continue;

				MergeItem* mi = (MergeItem*)halloc( heap_gen, sizeof(MergeItem)/4 );

				mi->location = cp->location;
				mi->value = cp->value;
				mi->set0 = cp->sets[i].setID;
				mi->set1 = cp->sets[j].setID;

				atomic_inc( counter );
			}
		}
	}
	barrier(CLK_GLOBAL_MEM_FENCE);

	MergeItem* mil = (MergeItem*)(d_heap+2);


	for( int loop = 0; loop < (*counter); loop++ ){
		selectLargest( idx, mil+loop, (*counter)-loop );

		MergeItem* m0 = (mil+loop);
		if( m0->location == -1){
			break;
		}

		if( idx == loop ){
			joinDJS( d_djs, m0->set0, m0->set1 );
		}
		barrier(CLK_GLOBAL_MEM_FENCE);
		if( idx > loop ){
			MergeItem* mi = (mil+idx);
			if( mi->location >= 0 ){
				mi->set0 = findDJS( d_djs, mi->set0 );
				mi->set1 = findDJS( d_djs, mi->set1 );
				if( mi->set0 == mi->set1 ) invalidate_mi( mi );
			}
		}

	}

/*
*/



void swap_mi( MergeItem* m0, MergeItem* m1 ){
	swap_flt( &(m0->value), &(m1->value) );
	swap_int( &(m0->location), &(m1->location) );
	swap_int( &(m0->set0), &(m1->set0) );
	swap_int( &(m0->set1), &(m1->set1) );
}

void invalidate_mi( MergeItem* m0 ){
	m0->location = -1;
	m0->value = -FLT_MAX;
	m0->set0 = INT_MAX;
	m0->set1 = INT_MAX;
}


void selectLargest( int idx, MergeItem* mil, int listsize ){
	int iter = ilog2(listsize);
	for(int loop = 0; loop < iter; loop++){
		int filt = 2<<loop;
		int idx1 = idx + (1<<loop);
		if( idx1 < listsize && (idx%filt) == 0 ){
			MergeItem* m0 = (mil+idx);
			MergeItem* m1 = (mil+idx1);

			if( m0->value <  m1->value ){
				swap_mi( m0, m1 );
			}
		}
		barrier(CLK_GLOBAL_MEM_FENCE);
	}
}








int getSetOffsetByID( CriticalPoint * cp, int setID ){
	for(int i = 0; i < 8; i++){
		if( cp->sets[i].setID == -1 ){ return -1; }
		if( cp->sets[i].setID == setID ){ return i; }
	}
	return -1;
}

bool containsSetID( CriticalPoint * cp, int setID ){
	for(int i = 0; i < 8; i++){
		if( cp->sets[i].setID == -1 ){ return false; }
		if( cp->sets[i].setID == setID ){ return true; }
	}
	return false;
}





bool isSubset(  __global setList * set, __global setList * subset ){
	int i = 0, j = 0;
	while( set[i].setID != -1 && subset[j].setID != -1 ){
		if( set[i].setID == -2 ) {
			i++;
		}
		else if( subset[j].setID == -2 ){
			j++;
		}
		else if( set[i].setID == subset[j].setID ){
			i++;
			j++;
		}
		else if( set[i].setID < subset[j].setID ){
			i++;
		}
		else if( set[i].setID > subset[j].setID ){
			return false;
		}
	}
	return subset[j].setID < 0;
}


bool equalSets( __global setList * set0, __global setList * set1 ){
	int i = 0, j = 0;
	while( set0[i].setID != -1 || set1[j].setID != -1 ){
		if( set0[i].setID == -2 ) {
			i++;
		}
		else if( set1[j].setID == -2 ){
			j++;
		}
		else if( set0[i].setID != set1[j].setID ){
			return false;
		}
		else{
			i++;
			j++;
		}
	}
	return true;
}


void merge( __global int* d_heap, __global int* d_cps, int idx, int mul, int cnt, int data, int scratch ){

	int start  = idx;
	int middle = min(idx+mul/2,cnt);
	int end    = min(idx+mul,cnt);

	int i = start;
	int j = middle;

	for( int output = (scratch+start); output < (scratch+end); output++){
		CriticalPoint * pi = (d_cps+(*(d_heap+data+i)));
		CriticalPoint * pj = (d_cps+(*(d_heap+data+j)));
		if( (i < middle && pi->location < pj->location) || j >= end ){
			//*(d_heap + output) = pi->location;
			*(d_heap + output) = *(d_heap+data+i);
			i++;
		}
		else{
			//*(d_heap + output) = pj->location;
			*(d_heap + output) = *(d_heap+data+j);
			j++;
		}
	}

}

void sortMaximaByID( __global int * d_heap, int idx, __global int* d_cps ){
	int cnt     = (*d_heap)-1;
	int data    = 1;
	int scratch = (*d_heap)+4;
	int mul     = 2;

	int numIter = ceil(log2( (float)cnt ));
	numIter += numIter%2;

	for( int iter = 0; iter < numIter; iter++ ){
		if( idx < cnt && idx%mul==0 ){
			merge( d_heap, d_cps, idx, mul, cnt, data, scratch );
		}

		int tmp = data;
		data = scratch;
		scratch = tmp;
		mul = mul*2;
		barrier(CLK_GLOBAL_MEM_FENCE);
	}
}



CriticalPoint * bsearchMaximaByID( __global int * d_heap, __global int * d_cps, int setID ){

	int cnt     = (*d_heap)-1;
	int numIter = ceil(log2( (float)cnt ));

	int L = 0;
	int R = cnt-1;

	CriticalPoint * ret = 0;

	for(int i = 0; i < numIter && L<=R; i++ ){
		int m = (L+R)/2;

		__global int * i0 = d_heap+m+1;
		CriticalPoint * p0 = (d_cps+(*i0));

		if( setID == p0->location ){ ret = p0; }
		if( setID <  p0->location ){ R = m-1; }
		if( setID >  p0->location ){ L = m+1; }
	}

	return ret;

}


CriticalPoint * lsearchMaximaByID( __global int * d_heap, __global int * d_cps, int setID ){

	int cnt     = (*d_heap)-1;

	CriticalPoint * ret = 0;

	for(int i = 0; i < cnt; i++ ){

		__global int * i0 = d_heap+i+1;
		CriticalPoint * p0 = (d_cps+(*i0));

		if( setID == p0->location ){ ret = p0; }
	}

	return ret;

}


void binCriticalPoint( CriticalPoint * cp, __global int * d_heap, __global int * d_cps ){

    if( cp != 0 && cp->ref > 0 ){
    	for(int s = 0; s < cp->ref; s++){
    		if( cp->sets[s].setID == -1 ) break;
    		//CriticalPoint * root = bsearchMaximaByID( d_heap, d_cps, cp->sets[s].setID );
    		CriticalPoint * root = lsearchMaximaByID( d_heap, d_cps, cp->sets[s].setID );
    		if( root != 0 ){
    			//cp->sets[s].next = root->location;
    			int offset = (int)(((int*)cp)-d_cps);
    			cp->sets[s].next = atomic_xchg( &(root->sets[0].next), offset );
    		}
    		else{
    			cp->sets[s].next = -2;
    		}
    	}
    }
    barrier(CLK_GLOBAL_MEM_FENCE);

}

void orderLists( CriticalPoint * cp, __global int * d_heap, __global int * d_cps );

void orderLists( CriticalPoint * cp, __global int * d_heap, __global int * d_cps ){

	float maxVal[]  = {-FLT_MAX,-FLT_MAX,-FLT_MAX,-FLT_MAX,-FLT_MAX,-FLT_MAX,-FLT_MAX,-FLT_MAX,-FLT_MAX,-FLT_MAX};
	int   maxChld[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};


	if( cp != 0 ) {

		for(int s = 0; s < 10; s++ ){

			int currSetID = cp->sets[s].setID;

			if( currSetID < 0 ) break;

			CriticalPoint * root = lsearchMaximaByID( d_heap, d_cps, cp->sets[s].setID );
			int i0 = root->sets[0].next;

			int cnt = 0;
			while( i0 != -1 && cnt < 20 ){
				CriticalPoint * child = (CriticalPoint*)(d_cps+i0);
				if( child->value < cp->value && child->value > maxVal[s] ){
					maxVal[s]  = child->value;
					maxChld[s] = i0;
				}

				int setoff = getSetOffsetByID( child, currSetID );
				i0 = child->sets[setoff].next;
				cnt++;
			}
		}
	}
    barrier(CLK_GLOBAL_MEM_FENCE);

	if( cp != 0 ) {
		for(int s = 0; s < 10; s++ ){

			int currSetID = cp->sets[s].setID;

			if( currSetID < 0 ) break;
			cp->sets[s].next = maxChld[s];
		}
	}

}



CriticalPoint * peelRootNodes( CriticalPoint * cp, __global int * d_heap, __global int * d_cps );

CriticalPoint * peelRootNodes( CriticalPoint * cp, __global int * d_heap, __global int * d_cps ){

    if( cp != 0 && cp->type == MAXIMA ){

		for(int i = 0; i < 8; i++){
			if( cp->sets[i].setID == -1 ) break;
			if( cp->sets[i].setID == -2 ) continue;

			CriticalPoint * child = (CriticalPoint*)(d_cps+cp->sets[i].next);
			atomic_dec( &(child->ref) );
			cp->sets[i].setID = 999;
		}

		cp->ref = -1;
		cp = 0;
	}

    return cp;


}


void addChild( CriticalPoint * cp, int setID, int next ){
	for( int j = 0; j < 8; j++){
		if( cp->sets[j].setID < 0 ){
			cp->sets[j].setID = setID;
			cp->sets[j].next  = next;
			return;
		}
	}
}

CriticalPoint * peelSaddles( CriticalPoint * cp, __global int * d_heap, __global int * d_cps, __global uint * djs );

CriticalPoint * peelSaddles( CriticalPoint * cp, __global int * d_heap, __global int * d_cps, __global uint * djs ){

	if( cp != 0 && cp->type == SADDLE && cp->ref == 0 ){

		int   childCount = 0;
		int   maxChildOff = -1;
		float maxChildVal = -FLT_MAX;
		int   newSet = -1;
		CriticalPoint* maxChild = 0;

		for( int j = 0; j < 8; j++){
			if( cp->sets[j].setID == -1 ) break;
			if( cp->sets[j].setID == -2 ) continue;
			if( cp->sets[j].next  <= 0 ) continue;

			if( newSet >= 0 ){
				newSet = joinDJS( djs, newSet, cp->sets[j].setID );
			}
			else{
				newSet = cp->sets[j].setID;
			}

			CriticalPoint * child = (CriticalPoint*)(d_cps+cp->sets[j].next);
			if( child->value > maxChildVal ){
				maxChild = child;
				maxChildVal = child->value;
				maxChildOff = cp->sets[j].next;
			}
			atomic_dec( &(child->ref) );

		}


		for( int j = 0; j < 8; j++){
			if( cp->sets[j].setID == -1 ) break;
			if( cp->sets[j].setID == -2 ) continue;
			if( cp->sets[j].next  <= 0 ) continue;

			CriticalPoint * child = (CriticalPoint*)(d_cps+cp->sets[j].next);
			if( cp->sets[j].next != maxChildOff ){
				addChild( maxChild, cp->sets[j].setID, cp->sets[j].next );
				atomic_inc( &(child->ref) );
			}
			cp->sets[j].setID = -2;
			cp->sets[j].next  = -1;
		}


		if( maxChildOff >= 0 ){
			cp->sets[0].setID = 999;
			cp->sets[0].next  = maxChildOff;

		}

		cp->ref = -1;

		cp = 0;
    }

    return cp;

}


CriticalPoint * updatePointers( CriticalPoint * cp, __global int * d_heap, __global int * d_cps, __global uint * djs ){

	if( cp != 0 && cp->type == SADDLE && cp->ref >= 0 ){

			for( int j = 0; j < 8; j++){
				if( cp->sets[j].setID == -1 ) break;
				if( cp->sets[j].setID == -2 ) continue;
				if( cp->sets[j].next  <= 0 ) continue;

				cp->sets[j].setID = findDJS( djs, cp->sets[j].setID );
			}


			for( int i = 0; i < 8; i++){
				if( cp->sets[i].setID == -1 ) break;
				if( cp->sets[i].setID == -2 ) continue;
				if( cp->sets[i].next  <= 0 ) continue;

				for( int j = i+1; j < 8; j++){
					if( cp->sets[j].setID == -1 ) break;
					if( cp->sets[j].setID == -2 ) continue;
					if( cp->sets[j].next  <= 0 ) continue;

					if( cp->sets[i].setID == cp->sets[j].setID &&
							cp->sets[i].next == cp->sets[j].next ){
								CriticalPoint * child = (CriticalPoint*)(d_cps+cp->sets[j].next);
								atomic_dec( &(child->ref) );
								cp->sets[j].setID = -2;
								cp->sets[j].next = -1;
					}
				}
			}

	}

    return cp;

}



CriticalPoint * updateReferenceCount( CriticalPoint * cp, __global int * d_heap, __global int * d_cps, __global uint * djs );

CriticalPoint * updateReferenceCount( CriticalPoint * cp, __global int * d_heap, __global int * d_cps, __global uint * djs ){

	if( cp != 0 ){

		atomic_inc( &(cp->ref) );

		for(int i = 0; i < 8; i++){
			if( cp->sets[i].setID >= 0 && cp->sets[i].next >= 0 ){
				CriticalPoint * child = (CriticalPoint*)(d_cps+cp->sets[i].next);
				atomic_inc( &(child->ref) );
			}
		}
    }

    return cp;

}


CriticalPoint * shortCircuit( CriticalPoint * cp, __global int * d_heap, __global int * d_cps, __global uint * djs );

CriticalPoint * shortCircuit( CriticalPoint * cp, __global int * d_heap, __global int * d_cps, __global uint * djs ){

	if( cp != 0 ){
		if( cp->ref >= 2 && cp->type == SADDLE ){

			int newNext = cp->sets[0].next;
			CriticalPoint * curr = cp;

			while( curr->sets[0].next >= 0 ){
				newNext = curr->sets[0].next;
				curr->sets[0].setID = -1;
				curr->sets[0].next = -1;
				curr = (CriticalPoint*)(d_cps+newNext);
				if( curr->ref != 1 ){
					break;
				}
			}

			if( newNext != cp->sets[0].next ){
				cp->sets[0].setID = 666;
				cp->sets[0].next  = newNext;
			}
		}
	}

    return cp;

}




CriticalPoint * removeSingles( CriticalPoint * cp, __global int * d_heap, __global int * d_cps, __global uint * djs );

CriticalPoint * removeSingles( CriticalPoint * cp, __global int * d_heap, __global int * d_cps, __global uint * djs ){

	if( cp != 0 ){
		if( cp->ref == 0 && cp->type == SADDLE ){
			for(int i = 0; i < 8; i++){
				if( cp->sets[i].next >= 0 ){
					CriticalPoint * child = (CriticalPoint*)(d_cps+cp->sets[i].next);
					atomic_dec( &(child->ref) );

				}
				cp->sets[i].setID = -1;
				cp->sets[i].next  = -1;
			}
		}
    }

    return cp;

}

	/*

	//MergeItem* mil = (MergeItem*)(d_heap+2);
	for( int loop = 0; loop < (*counter); loop++ ){
		int offset = loop%2;
		if( (idx+1) < (*counter)  && (idx%2) == offset ){
			MergeItem* m0 = (mil+idx);
			MergeItem* m1 = (mil+idx+1);

			if( m0->value <  m1->value ){
				swap_mi( m0, m1 );
			}
		}
		barrier(CLK_GLOBAL_MEM_FENCE);
	}



	for( int loop = 0; loop < (*counter); loop++ ){
		int offset = loop%2;
		if( (idx+1) < (*counter)  && (idx%2) == offset ){
			MergeItem* m0 = (mil+idx);
			MergeItem* m1 = (mil+idx+1);

			bool swap0 = ( m0->value <  m1->value );

			if( swap0 ){
				swap_mi( m0, m1 );
			}
		}
		barrier(CLK_GLOBAL_MEM_FENCE);
	}
	*/
	/*
	for( int loop = 0; loop < (*counter)*2; loop++ ){
		int offset = loop%2;
		if( (idx+1) < (*counter)  && (idx%2) == offset ){
			MergeItem* m0 = (mil+idx);
			MergeItem* m1 = (mil+idx+1);

			bool swap0 = ( m0->set0  >  m1->set0 );
			bool eq0   = ( m0->set0  == m1->set0 );
			bool swap1 = ( m0->set1  >  m1->set1 );
			bool eq1   = ( m0->set1  == m1->set1 );

			bool swap = (swap0) || (eq0&&swap1);

			if( swap ){
				swap_mi( m0, m1 );
			}

			if( eq0&&eq1 ){
				if( m0->value < m1->value ){
					invalidate_mi(m0);
				}
				else {
					invalidate_mi(m1);
				}
			}
		}
		barrier(CLK_GLOBAL_MEM_FENCE);
	}
	*/

	/*
	CriticalPoint * proc_cp = cp;

	proc_cp = peelRootNodes( proc_cp, d_heap, d_cps );
    barrier(CLK_GLOBAL_MEM_FENCE);



	for(int pass = 1; pass <= 4; pass++ ){
		proc_cp = peelSaddles( proc_cp, d_heap, d_cps, d_djs );
		barrier(CLK_GLOBAL_MEM_FENCE);
		updatePointers( proc_cp, d_heap, d_cps, d_djs );
		barrier(CLK_GLOBAL_MEM_FENCE);
	}

	*/

/*
	cp = updateReferenceCount( cp, d_heap, d_cps, d_djs );
	barrier(CLK_GLOBAL_MEM_FENCE);

	//cp = removeSingles( cp, d_heap, d_cps, d_djs );
	cp = shortCircuit( cp, d_heap, d_cps, d_djs );
	barrier(CLK_GLOBAL_MEM_FENCE);
*/




void buildTreeRemoveNonCP( CriticalPoint * curr, __global int * d_cps ){

	// get head for iterator
	int iter = curr->sets[0].next;
	int currSetID = curr->sets[0].setID;

	// empty list
	curr->sets[0].next = -1;

	// while list has elements
	while( iter != -1 ) {

		int curIter = iter;

		CriticalPoint * child;
		child = (d_cps+curIter);

		int setoff = getSetOffsetByID( child, currSetID );

		iter = child->sets[setoff].next;


		if( equalSets(curr->sets, child->sets) ){
			child->ref = -2;
			child->sets[setoff].next  = -1;
			child->sets[setoff].setID = -2;
		}
		else {
			child->sets[setoff].next = curr->sets[0].next;
			curr->sets[0].next = curIter;
		}


	}


}


void buildTree( CriticalPoint * curr, __global int * d_cps, __global int * d_heap ){

	if( curr == 0 ) return;
	if( curr->ref != 0 ) return;

	buildTreeRemoveNonCP( curr, d_cps );


	int currSetID = curr->sets[0].setID;


	float maxVal = -FLT_MAX;
	CriticalPoint * maxChld = 0;


	int i0 = curr->sets[0].next;

	while( i0 != -1 ){
		CriticalPoint * child;
		child = (d_cps+i0);
		if( child->value > maxVal ){
			maxVal = child->value;
			maxChld = child;
		}

		int setoff = getSetOffsetByID( child, currSetID );
		i0 = child->sets[setoff].next;

	}


	if( maxChld != 0 ){

		// output the edge to the heap
		int heap_p = atomic_add(d_heap,2);
		d_heap[ heap_p+0 ] = curr->location;
		d_heap[ heap_p+1 ] = maxChld->location;


		// make the remaining nodes children of the maxChld
		int newHead = -1;

		i0 = curr->sets[0].next;

		while( i0 != -1 ) {
			int curIter = i0;

			CriticalPoint * child;
			child = (d_cps+curIter);

			int setoff = getSetOffsetByID( child, currSetID );
			i0 = child->sets[setoff].next;

			// if !maxChld, add to the front of the list
			if( child != maxChld ){
				child->sets[setoff].next = newHead;
				newHead = curIter;
			}

		}

		int setoff = getSetOffsetByID( maxChld, currSetID );
		maxChld->sets[setoff].next = newHead;
		atomic_dec( &(maxChld->ref) );

	}

	// clear the node, signal processing complete
	curr->ref = -1;
	curr->sets[0].setID = -2;
	curr->sets[0].next  = -1;

}



void buildTreeSaddles( CriticalPoint * curr, __global int * d_cps ){

	if( curr == 0 ) return;
	if( curr->ref != 0 ) return;

	int currSetID = curr->sets[0].setID;

	for( int curSet = 1; curr->sets[curSet].setID != -1; curSet++ ){
		if( curr->sets[curSet].setID < 0 ) continue;

		int rplcSetID = curr->sets[curSet].setID;

		int i0 = curr->sets[curSet].next;

		for( int iter = 0; iter < 40; iter++ ){

			if( i0 == -1 ) break;

			CriticalPoint * child = (d_cps+i0);
			int setOff = getSetOffsetByID( child, rplcSetID );

			int curI = i0;
			i0 = child->sets[setOff].next;

			if( containsSetID( child, currSetID ) ){
				//child->sets[setOff].setID = 100+currSetID;
				child->sets[setOff].setID = -2;
				child->sets[setOff].next  = -1;
				atomic_dec( &(child->ref) );
			}
			else{
				child->sets[setOff].setID = currSetID;
				child->sets[setOff].next = curr->sets[0].next;
				curr->sets[0].next = curI;

			}

		}

		curr->sets[curSet].setID = -2;
		curr->sets[curSet].next  = -1;
	}

}



void countActiveCriticalPoints( CriticalPoint * curr, __global int * size ){

	if( curr == 0 ) return;
	if( curr->ref != 0 ) return;

	atomic_inc( size );


}




void removeDoubleEdges( CriticalPoint * cp, __global int * d_heap, __global int * d_cps );

void removeDoubleEdges( CriticalPoint * cp, __global int * d_heap, __global int * d_cps ){

	if( cp != 0 ) {

		for(int i = 0; i < 10; i++ ){
			if( cp->sets[i].setID == -1 ) break;
			if( cp->sets[i].setID == -2 ) continue;

			for(int j = i+1; j < 10; j++){
				if( cp->sets[j].setID == -1 ) break;
				if( cp->sets[j].setID == -2 ) continue;

				if( cp->sets[i].next == cp->sets[j].next ){
					CriticalPoint * child = (CriticalPoint*)(d_cps+cp->sets[i].next);
					atomic_dec( &(child->ref) );
					cp->sets[j].setID = -2;
					cp->sets[j].next  = -1;
				}
			}
		}
	}
    barrier(CLK_GLOBAL_MEM_FENCE);

}



void removePassThruNodes( CriticalPoint * cp, __global int * d_heap, __global int * d_cps );

void removePassThruNodes( CriticalPoint * cp, __global int * d_heap, __global int * d_cps ){

	int ref = ( cp == 0 ) ? -1 : cp->ref;
    barrier(CLK_GLOBAL_MEM_FENCE);

    if( ref == 0 || ref >= 2 ){

    	int newSetID[8];
    	int newChildren[8];
    	int curNew = 0;
		for(int i = 0; i < 8; i++ ){
			if( cp->sets[i].setID == -1 ) break;
			if( cp->sets[i].setID == -2 ) continue;

			CriticalPoint * child = (CriticalPoint*)(d_cps+cp->sets[i].next);
			int gchildN = 0;
			if( child->ref == 1 ){
				for(int j = 0; j < 8; j++ ){
					if( child->sets[j].setID == -1 ) break;
					if( child->sets[j].setID == -2 ) continue;
					gchildN++;

				}
			}

			if( child->ref == 1 && gchildN == 1 ){
				for(int j = 0; j < 8; j++ ){
					if( child->sets[j].setID == -1 ) break;
					if( child->sets[j].setID == -2 ) continue;

					newChildren[curNew] = child->sets[j].next;
					newSetID[curNew]    = child->sets[j].setID;
					curNew++;
					child->sets[j].setID = -2;
					child->sets[j].next = -1;
					child->ref = -1;
				}
			}
			else{
				newSetID[curNew]    = cp->sets[i].setID;
				newChildren[curNew] = cp->sets[i].next;
				curNew++;
			}
		}

		for( int i = 0; i < curNew; i++ ){
			cp->sets[i].setID = newSetID[i];
			cp->sets[i].next  = newChildren[i];
		}
		for(int i = curNew; i < 8; i++){
			cp->sets[i].setID = -1;
			cp->sets[i].next  = -1;
		}

	}
    barrier(CLK_GLOBAL_MEM_FENCE);

}




CriticalPoint * updatePointers( CriticalPoint * cp, __global int * d_heap, __global int * d_cps, __global uint * djs );

CriticalPoint * updatePointers( CriticalPoint * cp, __global int * d_heap, __global int * d_cps, __global uint * djs ){

    if( cp != 0 ){

		for(int i = 0; i < 8; i++ ){
			if( cp->sets[i].setID == -1 ) break;
			if( cp->sets[i].setID == -2 ) continue;
			cp->sets[i].setID = findDJS( djs, cp->sets[i].setID );
		}

		for(int i = 0; i < 8; i++ ){
			if( cp->sets[i].setID == -1 ) break;
			if( cp->sets[i].setID == -2 ) continue;

			int maxChildOff = cp->sets[i].next;
			CriticalPoint * maxChild = (CriticalPoint*)(d_cps+cp->sets[i].next);

			for(int j = i+1; j < 10; j++){
				if( cp->sets[j].setID == -1 ) break;
				if( cp->sets[j].setID == -2 ) continue;

				if( cp->sets[j].setID == cp->sets[i].setID ){
					CriticalPoint * child = (CriticalPoint*)(d_cps+cp->sets[j].next);
					if( maxChild->value > child->value ){
						atomic_dec( &(child->ref) );
					}
					else{
						atomic_dec( &(maxChild->ref) );
						maxChildOff = cp->sets[j].next;
						maxChild    = child;
					}
					cp->sets[j].setID = -2;
					cp->sets[j].next  = -1;
				}
			}

			cp->sets[i].next = maxChildOff;
		}

    }

    barrier(CLK_GLOBAL_MEM_FENCE);

    return cp;

}
































int getCriticalPointType( __global float * data, int u, int v, int w, int h );

int getCriticalPointType( __global float * data, int u, int v, int w, int h ){

	float myVal = getValue( data, u, v, w, h );

	float ring[8];
	int   iring[8];
	for( int i = 0; i < 8; i++ ){
		int cu = u+neighborU[i];
		int cv = v+neighborV[i];

		if( validLocation( cu, cv, w, h ) ) {
			ring[i]  = getValue( data, cu, cv, w, h );
			iring[i] = (ring[i]<myVal) ? -1 : 1;
		}
		else {
			ring[i]  = myVal;
			iring[i] = 0;
		}
	}

	bool boundary = false;
	bool above = false;
	bool below = false;
	int transition = 0;
	for(int i = 0; i < 8; i++){
		if (iring[i]<0){ below = true; }
		if (iring[i]>0){ above = true; }

		if( iring[i] == 0 || iring[(i+1)%8] == 0){
			boundary = true;
		}
		else if( iring[i] != iring[(i+1)%8] )
			transition++;
	}


	if( below && transition == 0 ) return MAXIMA;
	if( above && transition == 0 ) return MINIMA;
	if(!boundary && transition >= 4 ) return SADDLE;
	if( boundary && transition >= 2 ) return SADDLE;
	return NORMAL;

}


GCriticalPoint * createInvalidCP( Heap heap_cps, int idx );

GCriticalPoint * createInvalidCP( Heap heap_cps, int idx ){

	GCriticalPoint * newCP = 0;
	newCP = (GCriticalPoint *)halloc( heap_cps, sizeof(GCriticalPoint)/4 ) ;

	newCP->location = idx;
	newCP->value    = -FLT_MAX;
	newCP->ref      = -1;
	//newCP->type     = NORMAL;

	for(int i = 0; i < 8; i++){
		newCP->setID[i] = -1;
	}

	return newCP;
}


bool groupRemove( __global int * set, int val );

bool groupRemove( __global int * set, int val ){
	bool removed = false;
	int j = 0;
	for(int i = 0; i < 8; i++){
		set[j] = set[i];

		if( set[i] == val ){
			removed = true;
		}
		else{
			j++;
		}

		if( set[i] == -1 ) return removed;
	}
	return removed;

}

bool groupInsert( __global int * set, int val );

bool groupInsert( __global int * set, int val ){
	for(int i = 0; i < 8; i++){
		// value already exists, do nothing
		if( set[i] == val ){
			return false;
		}
		// reached the end of the list, insert
		if( set[i] == -1 ){
			set[i] = val;
			return true;
		}
		// this will sort points, basically bubble sort
		if( set[i] > val ){
			int tmp = set[i];
			set[i] = val;
			val = tmp;
		}
	}
	return false;
}




bool set_insert_l( LCriticalPoint * curr, int val );
bool set_insert_l( LCriticalPoint * curr, int val ){
	for(int i = 0; i < 6; i++){
		// value already exists, do nothing
		if( curr->setID[i] == val ){
			return false;
		}

		// reached the end of the list, insert
		if( curr->setID[i] == -1 ){
			curr->setID[i] = val;
			curr->setN++;
			return true;
		}
		// this will sort points, basically bubble sort
		if( curr->setID[i] > val ){
			int tmp = curr->setID[i];
			curr->setID[i] = val;
			val = tmp;
		}
	}
	return false;
}

void set_fix_l( LCriticalPoint * curr );
void set_fix_l( LCriticalPoint * curr ){
	int setN = curr->setN;
	curr->setN = 0;
	for(int i = 0; i < setN; i++ ){
		for( int j = i+1; j < setN; j++ ){
			if( curr->setID[i] <  curr->setID[j] ){
				swap_int_l( &(curr->setID[i]), &(curr->setID[j]) );
			}
			else if( curr->setID[i] == curr->setID[j] ){
				curr->setID[j] = -1;
			}
		}
		if( curr->setID[i] >= 0 ){
			curr->setN++;
		}
	}
}

void set_update_l( LCriticalPoint * curr, __global uint * d_djs );
void set_update_l( LCriticalPoint * curr, __global uint * d_djs ){
	for(int i = 0; i < curr->setN; i++){
		curr->setID[i] = findDJS( d_djs, curr->setID[i] );
	}
}




void cp_init_l( LCriticalPoint * cp_loc );
void cp_init_l( LCriticalPoint * cp_loc ){
	cp_loc->location = -1;
	cp_loc->value    = -FLT_MAX;
	cp_loc->setN     = 0;
	for(int i = 0; i < 6; i++){
		cp_loc->setID[i] = -1;
	}
}



void cp_extract_l( __global float * data, __global uint * djs, int u, int v, int w, int h, LCriticalPoint * cp_loc );
void cp_extract_l( __global float * data, __global uint * djs, int u, int v, int w, int h, LCriticalPoint * cp_loc ){

	cp_init_l( cp_loc );

    if( !validLocation(u,v,w,h) ) return;

    int  cp_type  = getCriticalPointType( data,u,v,w,h );

    if( cp_type != SADDLE ) return;

	float myVal	  = getValue( data,u,v,w,h );

	for(int i = 0; i < 8; i++){
		int _u = u+neighborU[i];
		int _v = v+neighborV[i];
		if( validLocation( _u, _v, w, h ) ){
			float otherVal = getValue( data, _u,_v, w, h );
			if( otherVal >= myVal ){
				int sid = findDJS( djs, getIndex(_u,_v,w,h) );
				set_insert_l( cp_loc, sid );
			}
		}
	}

	if( cp_loc->setN >= 2 ){
		cp_loc->location = getIndex( u,v,w,h );
		cp_loc->value    = myVal;
	}

}




