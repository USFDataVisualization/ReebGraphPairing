

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

import junyi.reebgraph.pairing.conventional.ConventionalPairing;
import usf.saav.common.SystemX;
import usf.saav.common.Timer;
import usf.saav.topology.reebgraph.Conditioner;
import usf.saav.topology.reebgraph.ReebGraph;



public class PairingConventional {

	public static void main(String[] args) {
		
		for( String ip : args ) {
			try {
				System.out.println(ip);
				conventionalPairing( ip, true );
				System.out.println();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void conventionalPairing( String inputfile, boolean verbose ) throws Exception {
		
		float norm_epsilon = 0.01f;
		Timer t = new Timer();
		
		if( verbose ) System.out.println("CONVENTIONAL");
		
		t.start();
		ReebGraph rm1 = ReebGraphLoader.load(inputfile);
		if( verbose ) System.out.println("Load time: " + t.end() + "ms");
		
		t.start();
		if( verbose ) SystemX.writeStringToFile(rm1.toDot(), ConventionalPairing.tmp_directory + "graph.dot" );
		if( verbose ) System.out.println("Save time: " + t.end() + "ms");
		
		t.start();
		Conditioner rn1 = new Conditioner(rm1, norm_epsilon );
		if( verbose ) System.out.println("Normalize time: " + t.end() + "ms");
		
		t.start();
		if( verbose ) SystemX.writeStringToFile(rm1.toDot(), ConventionalPairing.tmp_directory + "graph_norm.dot" );
		if( verbose ) System.out.println("Save time: " + t.end() + "ms");
		
		t.start();
		for( ReebGraph rg : rm1.extractConnectedComponents() ) {
			new ConventionalPairing( rg );
		}
		if( verbose ) System.out.println("Conventional computation time: " + t.getElapsed() + "ms");
		rn1.printPersistentDiagram();
		
	}
}


