

/*
 *	Copyright (C) 2017 Visualization & Graphics Lab (VGL), USF
 *
 *	This file is part of libRGSimp, a library to compute persistence of Reeb graphs.
 *
 *	libRGSimp is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU Lesser General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	libRGSimp is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public License
 *	along with libRG.  If not, see <http://www.gnu.org/licenses/>.
 *
 *	Author(s):	Junyi Tu
 *	Version	 :	1.0
 *
 *	Modified by : -- 
 *	Date : --
 *	Changes  : --
 */
package junyi.reebgraph.cmd;

import junyi.reebgraph.ReebGraphLoader;
import junyi.reebgraph.ReebGraphNormalizer;
import junyi.reebgraph.pairing.merge.MergePairing;
import usf.saav.common.Timer;
import usf.saav.topology.reebgraph.ReebGraph;



public class PairingMerge {

	public static void main(String[] args) {
		
		for( String ip : args ) {
			try {
				System.out.println(ip);
				conventionalMerge( ip, false );
				System.out.println();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void conventionalMerge( String inputfile, boolean verbose ) throws Exception {
		
		float norm_epsilon = 0.01f;
		Timer t = new Timer();
		
		if( verbose ) System.out.println();
		if( verbose ) System.out.println("OUR APPROACH");
		
		ReebGraph rm2 = new ReebGraphLoader(inputfile);
		ReebGraphNormalizer rn2 = new ReebGraphNormalizer( rm2, norm_epsilon );

		t.start();
		new MergePairing( rm2 );
		if( verbose ) System.out.println("Our computation time: " + t.end() + "ms");
		
		rn2.printPersistentDiagram();
		
	}
}


