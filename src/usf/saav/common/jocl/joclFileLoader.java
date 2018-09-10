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

import java.io.File;
import java.io.IOException;

import usf.saav.common.SystemX;

public class joclFileLoader extends joclLoader {

	public joclFileLoader( String dir, String file ) throws IOException {
		loadFile( dir, file );
	}

	public joclFileLoader( String path ) throws IOException {
		this( new File(path) );
	}

	public joclFileLoader( File path ) throws IOException {
		loadFile( path.getParentFile().getAbsolutePath(), path.getName() );
	}

	@Override
	protected String[] getContents(String dir, String file) throws IOException {
		return SystemX.readFileContents( new File(dir+"/"+file) );
	}
		
}
