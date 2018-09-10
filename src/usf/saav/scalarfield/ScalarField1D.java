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
import java.io.PrintWriter;


public interface ScalarField1D extends ScalarFieldND {

	int getWidth();
	
	
	public class Empty extends ScalarField1D.Default {
		int w;
		float default_val;
		public Empty( int w, float default_val ) {
			this.w = w;
			this.default_val = default_val;
		}
		@Override public int getSize() { return w; }
		@Override public float getValue(int x) { return default_val; }
		@Override public int getWidth() { return w; }
	}
	
	public abstract class Default extends ScalarFieldND.Default implements ScalarField1D {

		protected Default( ){ }

		public static void saveField(ScalarField1D sf, String filename) throws IOException {
			PrintWriter pw = new PrintWriter( filename );
			pw.println( "1D " + sf.getWidth() );
			for(int i = 0; i < sf.getWidth(); i++){
				pw.print( sf.getValue(i) + " " );
			}
			pw.close();
		}

	}
	
	

}
