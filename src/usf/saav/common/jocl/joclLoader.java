/*
 *     saav-core - A (very boring) software development support library.
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
package usf.saav.common.jocl;

import java.io.IOException;
import java.util.Vector;

public abstract class joclLoader {

	Vector< String > program = new Vector< String >( );
	
	protected abstract String [] getContents( String dir, String file ) throws IOException;
	
	protected void loadFile( String dir, String file ) throws IOException {
		String [] program = null;
		try {
			program = getContents( dir, file );
		}
		catch( IOException e ){
			e.printStackTrace();
		}
		
		for(String s : program ){
			if( !s.startsWith("#include") ){
				this.program.add(s);
			}
			else {
				String inc = s.substring( 8 ).trim();
				inc = inc.substring(1, inc.length()-1 );
				loadFile( dir, inc );
			}
		}
	}
	
	@Override
	public String toString( ){
		StringBuilder newProgram = new StringBuilder();
		for(String s : program){
			newProgram.append( s + "\n" );
		}
		return newProgram.toString();
	}
}
