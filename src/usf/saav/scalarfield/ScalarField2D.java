/*
 *     jPSimp - Persistence calculation and simplification of scalar fields.
 *     Copyright (C) 2016 PAUL ROSEN
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     You may contact the Paul Rosen at <prosen@usf.edu>.
 */
package usf.saav.scalarfield;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Vector;


public interface ScalarField2D extends ScalarFieldND {

	
	

	public int getWidth();
	public int getHeight();
	
	public float     getValue( int x, int y );
	public double [] getCoordinate( int x, int y );
	
	

	public class Padded extends ScalarField2D.Default {
		int w,h;
		ScalarField2D base;
		
		public Padded(  ScalarField2D _base, int padX, int padY ) {
			base = _base;
			w = _base.getWidth()+padX-1;
			h = _base.getHeight()+padY-1;
			w -= (w%padX);
			h -= (h%padY);
			System.out.println( _base.getWidth() + " " + _base.getHeight() + " == " + w + " " + h );
		}

		@Override public int getWidth() { return w; }
		@Override public int getHeight() { return h; }

		@Override
		public float getValue(int x, int y) { 
			if( x < base.getWidth() && y < base.getHeight() ) 
				return base.getValue(x, y);
			return 0;
		}


	}

	

	public abstract class Default extends ScalarFieldND.Default implements ScalarField2D {

		protected Default( ){ }
		
		@Override public double [] getCoordinate( int x, int y ){ return new double[]{x,y}; }
		@Override public int getSize() { return getWidth()*getHeight(); }
		@Override public float getValue(int nodeID) { return getValue( nodeID%getWidth(), nodeID/getWidth() ); }
		
		public boolean isValid( int nodeID ){
			return !Float.isNaN( getValue( nodeID ) );
		}

		@Override
		public String toString( ){
			StringBuilder ret = new StringBuilder(); 
			int w = getWidth();
			for(int i = 0; i < getSize(); i++){
				ret.append( String.format("%1.6f", getValue(i) ) );
				ret.append( " " );
				if( (i%w)==(w-1) ) ret.append('\n');
			}
			return ret.toString();
		
		}

	}	
	
	public class Empty extends ScalarField2D.Default {
		int w,h;
		float default_val;
		public Empty( int w, int h, float default_val ) {
			this.w = w;
			this.h = h;
			this.default_val = default_val;
		}
		public Empty( int w, int h, float default_val, boolean verbose ) {
			this.w = w;
			this.h = h;
			this.default_val = default_val;
		}
		@Override public int getWidth()  {	return w; }
		@Override public int getHeight() {	return h; }
		@Override public float getValue(int x, int y) { return default_val; }
	}
	

	public class ArrayField extends ScalarField2D.Default {
		
		float [] data;
		int width, height;
		
		public ArrayField( int w, int h ){
			data = new float[w*h];
			width = w;
			height = h;
		}

		public ArrayField(String filename) throws IOException {
			BufferedReader reader = new BufferedReader( new FileReader(filename) );
			String [] wh = reader.readLine().split("\\s+");
			width  = Integer.valueOf(wh[0]);
			height = Integer.valueOf(wh[1]);
			data = new float[width*height];
			for( int y = 0; y < height; y++ ){
				String [] ltmp = reader.readLine().split("\\s+");
				for(int x = 0; x < ltmp.length && x < width; x++ ){
					data[ y*width + x ] = Float.valueOf( ltmp[x] );
				}
			}
			reader.close();
		}

		@Override public int getWidth() { return width; }
		@Override public int getHeight() { return height; }
		@Override public float getValue(int x, int y) { return data[y*width+x]; }
		@Override public float getValue(int idx) { return data[idx]; }


	}

	
	public class MedianFilterField extends ScalarField2D.ArrayField {
		
		public MedianFilterField( ScalarField2D src, int boxsize ){
			super(src.getWidth(),src.getHeight());
		
			float [] tmp = new float[boxsize*boxsize];
			
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					int cur = 0;
					for(int _y = -boxsize/2; _y < boxsize/2 && (y+_y) < height; _y++){
						for(int _x = -boxsize/2; _x < boxsize/2 && (x+_x) < width; _x++){
							if( (x+_x)>=0 && (y+_y)>=0 )
								tmp[cur++] = src.getValue(x+_x, y+_y);
						}
					}
					for(int i = cur; i < boxsize*boxsize; i++){
						tmp[i] = Float.MAX_VALUE;
					}
					Arrays.sort(tmp);
					data[y*width+x] = tmp[cur/2];

				}
			}


		}


	}
	
	
	public class UniqueField extends ScalarField2D.ArrayField {
		
		
		public UniqueField( ScalarField2D src ){
			super(src.getWidth(),src.getHeight());
						
			Vector<val_sorter> ind = new Vector<val_sorter>();
			for(int i = 0; i < data.length; i++){
				data[i] = src.getValue(i);
				ind.add(new val_sorter(i));
			}
			Collections.sort( ind, new Comparator<val_sorter>(){
				@Override
				public int compare(val_sorter arg0, val_sorter arg1) {
					if( data[arg0.idx] < data[arg1.idx] ) return -1;
					if( data[arg0.idx] > data[arg1.idx] ) return  1;
					return 0;
				}				
			});

			for(int i = 0; i < ind.size(); i++){
				data[ind.get(i).idx] = (float)i/(float)(ind.size()-1)*100.0f;
			}

		}
		
		class val_sorter {
			int idx;
			val_sorter( int idx ){
				this.idx = idx;
			}
		}


	}

	public class MeanFilterField extends ScalarField2D.ArrayField {
		
		public MeanFilterField( ScalarField2D src, int boxsize ){
			super(src.getWidth(),src.getHeight());

			
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					float tmp = 0;
					int cur = 0;
					for(int _y = -boxsize/2; _y < boxsize/2 && (y+_y) < height; _y++){
						for(int _x = -boxsize/2; _x < boxsize/2 && (x+_x) < width; _x++){
							if( (x+_x)>=0 && (y+_y)>=0 ){
								tmp += src.getValue(x+_x, y+_y);
								cur++;
							}
							
						}
					}
					data[y*width+x] = tmp / (float)cur;

				}
			}


		}

	}
	
	
	public class RandomField extends ScalarField2D.ArrayField {
		
		public RandomField( int w, int h ){
			this(w,h,0);
		}

		public RandomField( int w, int h, long randSeed ){
			super(w,h);
			Random random = new Random();
			random.setSeed(randSeed);
			for(int i = 0; i < data.length; i++){
				data[i] = random.nextFloat()*100;
			}
			
			for( int res = 2; res < Math.min(w, h); res*=2 ){
				for(int y = 0; y < h; y+=res){
					for(int x = 0; x < w; x+=res){
						float r = (random.nextFloat()*random.nextFloat()+random.nextFloat())*100/res;
						for(int _y = 0; _y < res && (y+_y) < h; _y++){
							for(int _x = 0; _x < res && (x+_x) < w; _x++){
								data[(y+_y)*w+(x+_x)] += r;
							}
						}
					}
				}
			}	
			
		}

	}
	
	
}