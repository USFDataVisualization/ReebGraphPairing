

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
package usf.saav.cmd;

import java.util.ArrayList;

import usf.saav.common.TimerNanosecond;
import usf.saav.topology.reebgraph.ReebGraph;
import usf.saav.topology.reebgraph.pairing.MergePairing;



public class MergePairingCLI {

	public static void main(String[] args) {
		
		if( args.length == 0 ) {
			System.out.println("");
			System.out.println("   ###################################################################################");
			System.out.println("   Propagate and pair: A single-pass approach to critical point pairing in reeb graphs");
			System.out.println("   International Symposium on Visual Computing, Springer, Cham, 2019");
			System.out.println("   Junyi Tu, Mustafa Hajij, and Paul Rosen");
			System.out.println("");
			System.out.println("   Usage:");
			System.out.println("      > java -jar ReebGraphPairingMP.jar <file1> <file2> ... <fileN>");
			System.out.println("");
		}
		else {
			for( String ip : args ) {
				try {
					System.out.println(ip);
					ArrayList<ReebGraph> rg = TestResults.runAlgo( ip, new MergePairing(), new TimerNanosecond(), false );
					TestResults.printPersistentDiagramCSV( rg );
					System.out.println();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}


