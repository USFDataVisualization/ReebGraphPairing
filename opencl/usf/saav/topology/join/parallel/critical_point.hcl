#ifndef __CRITICAL_POINT_HCL__
#define __CRITICAL_POINT_HCL__

#define MAXIMA  1
#define MINIMA  2
#define SADDLE  3
#define NORMAL  0

int constant neighborU[8] = {-1, -1, -1,  0,  1,  1,  1,  0 };
int constant neighborV[8] = {-1,  0,  1,  1,  1,  0, -1, -1 };


struct tag_CriticalPoint {
	int location;
	float value;
	int ref;
	int setID[8];
};

typedef __global struct tag_CriticalPoint GCriticalPoint;


typedef __local struct tag_LocalCriticalPoint {
	int location;
	float value;
	int setN;
	int setID[6];
} LCriticalPoint;



void swap_cp_g( GCriticalPoint *m0, GCriticalPoint * m1 );
void swap_cp_g( GCriticalPoint *m0, GCriticalPoint * m1 ){
		swap_int_g( &(m0->location), &(m1->location) );
		swap_flt_g( &(m0->value), &(m1->value) );
		swap_int_g( &(m0->ref), &(m1->ref) );
		for(int i = 0; i < 8; i++){
			swap_int_g( &(m0->setID[i]), &(m1->setID[i]) );
		}
}

void swap_cp_l( LCriticalPoint *m0, LCriticalPoint * m1 );
void swap_cp_l( LCriticalPoint *m0, LCriticalPoint * m1 ){
		swap_int_l( &(m0->location), &(m1->location) );
		swap_flt_l( &(m0->value), &(m1->value) );
		swap_int_l( &(m0->setN), &(m1->setN) );
		for(int i = 0; i < 6; i++){
			swap_int_l( &(m0->setID[i]), &(m1->setID[i]) );
		}
}


void move_cp_l2g( LCriticalPoint *from, GCriticalPoint * to );
void move_cp_l2g( LCriticalPoint *from, GCriticalPoint * to ){
	to->location = from->location;
	to->value = from->value;
	to->ref = from->setN;
	for(int i = 0; i < 6; i++){
		to->setID[i] = from->setID[i];
	}
	for(int i = 6; i < 8; i++){
		to->setID[i] = -1;
	}
}

void move_cp_g2l( GCriticalPoint *from, LCriticalPoint * to );
void move_cp_g2l( GCriticalPoint *from, LCriticalPoint * to ){
	to->location = from->location;
	to->value = from->value;
	to->setN = from->ref;
	for(int i = 0; i < 6; i++){
		to->setID[i] = from->setID[i];
	}
}

void move_cp_g2g( GCriticalPoint *from, GCriticalPoint * to );
void move_cp_g2g( GCriticalPoint *from, GCriticalPoint * to ){
	to->location = from->location;
	to->value = from->value;
	to->ref = from->ref;
	for(int i = 0; i < 8; i++){
		to->setID[i] = from->setID[i];
	}
}


bool checkForSaddle( int iring[8] );
bool checkForSaddle( int iring[8] ){
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

	if(!boundary && transition >= 4 ) return true;
	if( boundary && transition >= 2 ) return true;
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
		if( curr->setID[i] < val ){
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
			if( curr->setID[i] < curr->setID[j] ){
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


void set_fix_g( GCriticalPoint * curr );
void set_fix_g( GCriticalPoint * curr ){
	int setN = curr->ref;
	curr->ref = 0;
	for(int i = 0; i < setN; i++ ){
		for( int j = i+1; j < setN; j++ ){
			if( curr->setID[i] < curr->setID[j] ){
				swap_int_g( &(curr->setID[i]), &(curr->setID[j]) );
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

	int   iring[8];
	int   sid[8];

	float myVal = getValue( data, u, v, w, h );

	for( int i = 0; i < 8; i++ ){
		int cu = u+neighborU[i];
		int cv = v+neighborV[i];

		iring[i]  =  0;
		sid[i]    = -1;

		if( validLocation( cu, cv, w, h ) ) {
			float curVal = getValue( data, cu, cv, w, h );
			iring[i]     = (curVal<myVal) ? -1 : 1;
			if( curVal >= myVal ){
				sid[i] = djs[getIndex(cu,cv,w,h)];
			}
		}
	}


	cp_init_l( cp_loc );

	if( checkForSaddle(iring) ){
		cp_loc->location = getIndex( u,v,w,h );
		cp_loc->value    = myVal;

		for(int i = 0; i < 8; i++){
			if( sid[i] >= 0 ){
				set_insert_l( cp_loc, sid[i] );
			}
		}
	}


}


#endif
