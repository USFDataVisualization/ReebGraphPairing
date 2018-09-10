#ifndef __COMMON_HCL__
#define __COMMON_HCL__


int getIndex( int x, int y, int w, int h );
int getIndex( int x, int y, int w, int h ){
	return y*w+x;
}


bool validLocation( int x, int y, int w, int h );
bool validLocation( int x, int y, int w, int h ){
    return (x >= 0 && x<w && y>=0 && y<h );
}


float getValue( __global float * data, int x, int y, int w, int h );
float getValue( __global float * data, int x, int y, int w, int h ){
	return data[getIndex(x,y,w,h)];
}

float getValueIdx( __global float * data, int idx );
float getValueIdx( __global float * data, int idx ){
	return data[idx];
}


void swap_int_g( __global int* i0, __global int* i1 );
void swap_int_g( __global int* i0, __global int* i1 ){
	int tmp = *i0;
	*i0 = *i1;
	*i1 = tmp;
}


void swap_int_l( __local int* i0, __local int* i1 );
void swap_int_l( __local int* i0, __local int* i1 ){
	int tmp = *i0;
	*i0 = *i1;
	*i1 = tmp;
}

void swap_flt_g( __global float* i0, __global float* i1 );
void swap_flt_g( __global float* i0, __global float* i1 ){
	float tmp = *i0;
	*i0 = *i1;
	*i1 = tmp;
}

void swap_flt_l( __local float* i0, __local float* i1 );
void swap_flt_l( __local float* i0, __local float* i1 ){
	float tmp = *i0;
	*i0 = *i1;
	*i1 = tmp;
}


int ilog2( int val );
int ilog2( int val ){
	return (int)log2((float)val);
}


#endif
