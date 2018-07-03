

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;

import usf.saav.topology.reebgraph.ReebGraph;
import usf.saav.topology.reebgraph.ReebGraphVertex;


public class RandomLoopTestCLI {

	public static String [] testSet = new String[] {
			"100_tree_iterations.txt",
			"500_tree_iterations.txt",
			"1000_tree_iterations.txt",
			"3000_tree_iterations.txt",
			"5000_tree_iterations.txt"
	};
	
	
	public static void main( String[] args ) {
		int [] randCnt = new int[] {100,500,1000,3000,5000};
		int curr = 0;
		for( String ipp : testSet ) {
			try {
				int randCount = randCnt[curr++];
				String newip = ipp.substring(0, ipp.length()-4) + "-rand" + randCount + ".txt";
				generateFile( "test/"+ipp, "test/"+newip, randCount );
				
				int testCount = 10;
				
				double mergeTimer = 0;
				double ppTimer    = 0;
				int i = 0;
				TestResults result = null;
				for( ; i < testCount; i++ ) {
					result = TestResults.testPerformance( "test/"+newip, false );
					if( result == null ) {
						System.out.println( "test FAILED: " + newip );
						break;
					}
					if( result.ppTimer.getElapsedMilliseconds() < 50 ) testCount = 100;
					if( result.ppTimer.getElapsedMilliseconds() < 1 ) testCount = 1000;
					mergeTimer += result.mergeTimer.getElapsedMilliseconds();
					ppTimer += result.ppTimer.getElapsedMilliseconds();
				}
				if( i == testCount ) {
					
					TestResults.savePersistentDiagram( result.rgMP, "pd/" + newip );
					System.out.print( newip.split("\\.")[0].replace("_", "\\_") + " & & & " );
					System.out.print( result.initial_verts + " & " );
					System.out.print( result.conditioned_verts + " & ");
					System.out.print( result.loops + " & " );
					if( (mergeTimer/testCount) > 0.1 )
						System.out.printf("%.2f & ", (mergeTimer/testCount) );
					else
						System.out.printf("%.2e & ", (mergeTimer/testCount) );
					if( (ppTimer/testCount) > 0.1 )
						System.out.printf("%.2f \\\\ ", (ppTimer/testCount) );
					else
						System.out.printf("%.2e \\\\ ", (ppTimer/testCount) );
					System.out.println("\n\\hline");
					//System.out.println( "test succeeded: " + ip + " -- init_nodes: " + result.initial_verts + " cond_nodes: " + result.conditioned_verts + " loops: " + result.loops + " tests: " + testCount + " merge_time: " + (mergeTimer/testCount) + " pp_timer: " + (ppTimer/testCount) );
				}
	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	static void generateFile( String inputReebGraph, String outputReebGraph, int randCount ) throws Exception {

		
		HashMap<Integer, ReebGraphVertex> rvmap = new HashMap<Integer, ReebGraphVertex>();
		BufferedReader reader = new BufferedReader(new FileReader(inputReebGraph));
		ReebGraph tmpRG = new ReebGraph();
		String s;
		
		ReebGraphVertex maxV = null;
		Random rand = new Random();

		while( (s = reader.readLine()) != null) {
			String[] r = s.split("\\s");
			if( r.length == 0 ) continue;
			if (r[0].trim().equals("v") == true) {			     

				if(r.length != 3) {
					reader.close();
					throw new Exception("Invalid edge input");
				}

				int    v  = Integer.parseInt(r[1].trim());
				float  fn = Float.parseFloat(r[2].trim());

				ReebGraphVertex newR = new ReebGraphVertex( fn, fn, v );
				tmpRG.add( newR );
				rvmap.put( v, newR );
				
				if (maxV == null || newR.getRealValue() > maxV.getRealValue() ) maxV = newR;

			} 
			if (r[0].trim().equals("e") == true) {

				if(r.length != 3) {
					reader.close();
					throw new Exception("Invalid edge input");
				}

				ReebGraphVertex v1 = rvmap.get(Integer.parseInt(r[1]));
				ReebGraphVertex v2 = rvmap.get(Integer.parseInt(r[2]));

				if( v1 == null || v2 == null ) {
					reader.close();
					throw new Exception("WARNING: Edge not found " + r[1] + " " + r[2]);
				}

				v1.addNeighbor(v2);
				v2.addNeighbor(v1);
			}
		}
		
		reader.close();
		
		for( int i = 0; i < randCount; i++ ) {
			ReebGraphVertex rv0 = tmpRG.get( rand.nextInt(tmpRG.size() ) );
			ReebGraphVertex rv1 = walk( rand, rv0, rand.nextInt(100) );
			//ReebGraphVertex rv1 = tmpRG.get( rand.nextInt(tmpRG.size() ) );
			rv0.addNeighbor(rv1);
			rv1.addNeighbor(rv0);
		}				

		
		
		
		PrintWriter pw = new PrintWriter( outputReebGraph );
		
		for( ReebGraphVertex v : tmpRG ) {
			pw.println("v " + v.getGlobalID() + " " + v.getRealValue() );
		}
		for( ReebGraphVertex v : tmpRG ) {
			for( ReebGraphVertex n : v.neighbors ) {
				if( v.getGlobalID() < n.getGlobalID() )
				pw.println("e " + v.getGlobalID() + " " + n.getGlobalID() );
			}
		}
		
		pw.close();
				
		
	}
	
	private static ReebGraphVertex walk( Random random, ReebGraphVertex curr, int steps ) {
		if( steps == 0 ) return curr;
		return walk( random, curr.neighbors.get(random.nextInt( curr.neighbors.size() ) ), steps-1);
	}

}


