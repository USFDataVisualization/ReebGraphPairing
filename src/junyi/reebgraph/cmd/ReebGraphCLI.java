

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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import junyi.reebgraph.pairing.conventional.ConventionalPairing;
import usf.saav.common.SystemX;
import usf.saav.common.Timer;
import usf.saav.topology.reebgraph.Conditioner;
import usf.saav.topology.reebgraph.ReebGraph;
import usf.saav.topology.reebgraph.pairing.MergePairing;
import usf.saav.topology.reebgraph.pairing.PropagateAndPair;




public class ReebGraphCLI {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Properties p = new Properties();
			System.out.println(new File(".").getAbsolutePath());
			p.load(new FileInputStream("input.properties"));
			//String loaderType = p.getProperty("loader");
			String ip = p.getProperty("inputFile").trim();
			
			
			System.out.println( ip );
			Timer convTimer, mergeTimer, ppTimer;
			if( testPerformance( ip, (convTimer=new Timer()), (mergeTimer=new Timer()), (ppTimer=new Timer()), false ) ) {
				System.out.println( "test succeeded: " + ip + " -- conv_time: " + convTimer.getElapsed() + " merge_time: " + mergeTimer.getElapsed() + " pp_timer: " + ppTimer.getElapsed() );
			}
			else {
				System.out.println( "test FAILED: " + ip );
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Invalid input. Check the input.properties file");
			System.exit(0);
		}
	}


	public static boolean testPerformance( String inputfile, Timer convTimer, Timer mergeTimer, Timer ppTimer, boolean verbose ) throws Exception {
		float norm_epsilon = 0.01f;
		Timer t = new Timer();
		
		if( verbose ) System.out.println("CONVENTIONAL");
		
		t.start();
		ReebGraph rm1 = ReebGraphLoader.load(inputfile);
		if( verbose ) System.out.println("Load time: " + t.end() + "ms");
		
		t.start();
		SystemX.writeStringToFile(rm1.toDot(), ConventionalPairing.tmp_directory + "graph.dot" );
		if( verbose ) System.out.println("Save time: " + t.end() + "ms");
		
		t.start();
		Conditioner rn1 = new Conditioner(rm1, norm_epsilon );
		if( verbose ) System.out.println("Normalize time: " + t.end() + "ms");
		
		t.start();
		SystemX.writeStringToFile(rm1.toDot(), ConventionalPairing.tmp_directory + "graph_norm.dot" );
		if( verbose ) System.out.println("Save time: " + t.end() + "ms");
		
		convTimer.start();
		for( ReebGraph rg : rm1.extractConnectedComponents() ) {
			new ConventionalPairing( rg );
		}
		convTimer.end();
		if( verbose ) System.out.println("Conventional computation time: " + convTimer.getElapsed() + "ms");
		if( verbose ) rn1.printPersistentDiagram();
		
		
		
		if( verbose ) System.out.println();
		if( verbose ) System.out.println("Merge Pairing");
		
		ReebGraph rm3 = ReebGraphLoader.load(inputfile);
		Conditioner rn3 = new Conditioner( rm3, norm_epsilon );

		mergeTimer.start();
		new MergePairing( ).pair(rm3);
		mergeTimer.end();
		if( verbose ) System.out.println("Computation time: " + mergeTimer.getElapsed() + "ms");
		if( verbose ) rn3.printPersistentDiagram();
		rn3.printPersistentDiagram();
		
		
		
		
		if( verbose ) System.out.println();
		if( verbose ) System.out.println("OUR APPROACH");
		
		ReebGraph rm2 = ReebGraphLoader.load(inputfile);
		Conditioner rn2 = new Conditioner( rm2, norm_epsilon );

		ppTimer.start();
		new PropagateAndPair( ).pair(rm2);
		ppTimer.end();
		if( verbose ) System.out.println("Our computation time: " + ppTimer.getElapsed() + "ms");
		if( verbose ) rn2.printPersistentDiagram();
		rn2.printPersistentDiagram();
		
		
		if( verbose ) System.out.println();
		if( verbose ) System.out.println("COMPARING GRAPHS");
		if( !Conditioner.compareDiagrams(rn1, rn2, verbose ) ) {
			if( verbose ) System.out.println("ERROR: Difference Found in Graph Pairings (conv/p&p)");
			return false;
		}
		if( !Conditioner.compareDiagrams(rn1, rn3, verbose ) ) {
			if( verbose ) System.out.println("ERROR: Difference Found in Graph Pairings (conv/merge)");
			return false;
		}
		if( !Conditioner.compareDiagrams(rn2, rn3, verbose ) ) {
			if( verbose ) System.out.println("ERROR: Difference Found in Graph Pairings (p&p/merge)");
			return false;
		}
		return true;
	}
}


