#ifndef __HEAP_HCL__
#define __HEAP_HCL__

typedef struct tag_Heap {
	__global int * base;
} Heap;


Heap hinit( __global void * base );
Heap hload( __global void * base );
__global void * halloc( Heap heap, int size );


Heap hinit( __global void * base ){
	Heap ret;
	ret.base = base;
	atomic_max( ret.base, 1 );
	return ret;
}


Heap hload( __global void * base ){
	Heap ret;
	ret.base = base;
	return ret;
}


__global void * halloc( Heap heap, int size_ints ){
   	int offset = atomic_add( heap.base, size_ints );
   	return (__global void*)(heap.base+offset);
}

#endif
