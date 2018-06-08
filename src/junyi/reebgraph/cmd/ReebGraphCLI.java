

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

import junyi.reebgraph.ReebGraphLoader;
import junyi.reebgraph.ReebGraphNormalizer;
import junyi.reebgraph.pairing.conventional.ConventionalPairing;
import junyi.reebgraph.pairing.merge.MergePairing;
import usf.saav.common.SystemX;
import usf.saav.common.Timer;
import usf.saav.topology.reebgraph.ReebGraph;




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
			Timer convTimer, mergeTimer;
			if( testPerformance( ip, (convTimer=new Timer()), (mergeTimer=new Timer()), false ) ) {
				System.out.println( "test succeeded: " + ip + " -- conv_time: " + convTimer.getElapsed() + " merge_time: " + mergeTimer.getElapsed() );
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


	public static boolean testPerformance( String inputfile, Timer convTimer, Timer mergeTimer, boolean verbose ) throws Exception {
		float norm_epsilon = 0.01f;
		Timer t = new Timer();
		
		if( verbose ) System.out.println("CONVENTIONAL");
		
		t.start();
		ReebGraph rm1 = new ReebGraphLoader(inputfile);
		if( verbose ) System.out.println("Load time: " + t.end() + "ms");
		
		t.start();
		SystemX.writeStringToFile(rm1.toDot(), ConventionalPairing.tmp_directory + "graph.dot" );
		if( verbose ) System.out.println("Save time: " + t.end() + "ms");
		
		t.start();
		ReebGraphNormalizer rn1 = new ReebGraphNormalizer(rm1, norm_epsilon );
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
		if( verbose ) System.out.println("OUR APPROACH");
		
		ReebGraph rm2 = new ReebGraphLoader(inputfile);
		ReebGraphNormalizer rn2 = new ReebGraphNormalizer( rm2, norm_epsilon );

		mergeTimer.start();
		new MergePairing( rm2 );
		mergeTimer.end();
		if( verbose ) System.out.println("Our computation time: " + mergeTimer.getElapsed() + "ms");
		if( verbose ) rn2.printPersistentDiagram();
		
		
		if( verbose ) System.out.println();
		if( verbose ) System.out.println("COMPARING GRAPHS");
		if( !ReebGraphNormalizer.compareDiagrams(rn1, rn2, verbose ) ) {
			if( verbose ) System.out.println("ERROR: Difference Found in Graph Pairings");
			return false;
		}
		return true;
	}
}


