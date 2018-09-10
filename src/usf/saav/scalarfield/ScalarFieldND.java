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

import java.io.IOException;


public interface ScalarFieldND {

	public int   getSize( );
	public float getValue( int nodeID );
	

	
	public abstract class Default implements ScalarFieldND {
		
		protected Default(){ }

		public static double [] getValueRange( ScalarFieldND sf ){
			double min =  Double.MAX_VALUE;
			double max = -Double.MAX_VALUE;
			@SuppressWarnings("unused")
			int nanCnt = 0;
			for(int i = 0; i < sf.getSize(); i++){
				double v = sf.getValue(i);
				if( Double.isNaN(v) ) nanCnt++;
				else{
					min = Math.min(min, v);
					max = Math.max(max, v);
				}
			}
			return new double[]{min,max};
		}
		public double [] getValueRange(){
			double min =  Double.MAX_VALUE;
			double max = -Double.MAX_VALUE;
			@SuppressWarnings("unused")
			int nanCnt = 0;
			for(int i = 0; i < getSize(); i++){
				double v = getValue(i);
				if( Double.isNaN(v) ) nanCnt++;
				else{
					min = Math.min(min, v);
					max = Math.max(max, v);
				}
			}
			return new double[]{min,max};
		}
		
		public static void saveField(ScalarFieldND sf, String filename) throws IOException {
			if( sf instanceof ScalarField1D ){
				ScalarField1D.Default.saveField( (ScalarField1D)sf, filename);
			}
			if( sf instanceof ScalarField2D ){
				ScalarField2D.Default.saveField( (ScalarField2D)sf, filename);
			}
		}
	}

}
