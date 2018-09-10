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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import usf.saav.common.SystemX;

public class joclResourceLoader extends joclLoader {

	public joclResourceLoader(String resource_path, String resource_file ) throws IOException {
		loadFile( resource_path, resource_file );
	}

	protected String [] getContents( String dir, String file ) throws IOException {
		URL path = getClass().getResource(dir+"/"+file);
		if( path == null ) throw new FileNotFoundException("Resource " + dir+"/"+file + " not found");
		return SystemX.readFileContents( path.openStream() );
	}

}
