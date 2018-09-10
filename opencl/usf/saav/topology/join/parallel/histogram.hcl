#ifndef __HISTOGRAM_HCL__
#define __HISTOGRAM_HCL__


typedef struct tag_histogram_bin {
	int size;
	int offset;
} histogram_bin;

typedef __global struct tag_histogram {
	int   binN;
	float minV;
	float maxV;
	histogram_bin bins[16]; // default size, but may be longer or shorted, look at binN for size
} GHistogram ;


int getHistogramBucketBase( GHistogram * hist, int binID );
int getHistogramBucketBase( GHistogram * hist, int binID ){
	int offset = 0;
	for(int i = 0; i < binID; i++){
		offset += hist->bins[i].size;
	}
	return offset;
}

int getHistogramBucketSize( GHistogram * hist, int binID );
int getHistogramBucketSize( GHistogram * hist, int binID ){
	return hist->bins[binID].size;
}


int getHistogramBucket( GHistogram * hist, float val );
int getHistogramBucket( GHistogram * hist, float val ){
	float fpos = 1 - ( val-hist->minV ) / ( hist->maxV-hist->minV );
	int   ipos = (int)( hist->binN * fpos );
	return (int)clamp( ipos, 0, hist->binN-1 );
}


int incHistogramBucketSize( GHistogram * hist, int bucket );
int incHistogramBucketSize( GHistogram * hist, int bucket ){
	return atomic_inc( &(hist->bins[bucket].size) );
}


int incHistogramBucketOffset( GHistogram * hist, int bucket );
int incHistogramBucketOffset( GHistogram * hist, int bucket ){
	return atomic_inc( &(hist->bins[bucket].offset) );
}




#endif
