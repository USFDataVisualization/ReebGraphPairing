

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

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import usf.saav.common.SystemX;
import usf.saav.common.Timer;
import usf.saav.common.TimerMillisecond;
import usf.saav.topology.reebgraph.ReebGraph;
import usf.saav.topology.reebgraph.ReebGraphLoader;




public class SingleTestCLI {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Properties p = new Properties();
			p.load(new FileInputStream("input.properties"));
			
			String inputFile	 = p.getProperty("inputFile").trim();
			boolean verbose		 = p.getProperty("verbose").trim().equalsIgnoreCase("true");
			boolean saveGraphDot = p.getProperty("saveGraphDot").trim().equalsIgnoreCase("true");
			int repeat 			 = Integer.parseInt(p.getProperty("repeatTest").trim());
			String outputPath    = p.getProperty("outputPath").trim() + "/";
			
			System.out.println( "Input File: " + inputFile );
			
			if( saveGraphDot )
				saveGraph( inputFile, outputPath, verbose );
			
			double mergeTotal = 0, ppTotal = 0;
			
			for( int i = 0; i < repeat; i++ ) {
				TestResults results = TestResults.testPerformance(inputFile, verbose);
				if( results != null ) {
					System.out.println( "test succeeded: " + inputFile + " -- merge_time: " + results.mergeTimer.getElapsedMilliseconds() + " pp_timer: " + results.ppTimer.getElapsedMilliseconds() );
				}
				else {
					System.out.println( "test FAILED: " + inputFile );
				}
				mergeTotal += results.mergeTimer.getElapsedMilliseconds();
				ppTotal    += results.ppTimer.getElapsedMilliseconds();
				verbose = false;
			}
			
			System.out.println( "Average -- Merge: " + (mergeTotal/repeat) + ", P&P: " + (ppTotal/repeat) );
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Invalid input. Check the input.properties file");
			System.exit(0);
		}
	}

	public static void saveGraph( String inputfile, String outputDir, boolean verbose ) throws Exception {
		Timer t = new TimerMillisecond();
		t.start();
		ArrayList<ReebGraph> rm1 = ReebGraphLoader.load(inputfile,true,false,verbose);
		t.end();
		if( verbose ) System.out.println("  Load time: " + t.getElapsedMilliseconds() + "ms");
		
		t.start();
		for(int i = 0; i < rm1.size(); i++ ) {
			SystemX.writeStringToFile(rm1.get(i).toDot(), outputDir + "graph"+i+".dot" );
		}
		t.end();
		if( verbose ) System.out.println("  Save time: " + t.getElapsedMilliseconds() + "ms");		
	}
	





}


