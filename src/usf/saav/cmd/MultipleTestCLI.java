

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


public class MultipleTestCLI {

	public static String [] testSet = new String[] {
			"1000_graph_iterations.txt", 
			"1000_tree_iterations.txt", 
			"100_iterations.txt", 
			"100_tree_iterations.txt", 
			"10_tree_iterations.txt", 
			"2000_graph_iterations.txt", 
			"2000_tree_iterations.txt", 
			"200_tree_iterations.txt", 
			"25_iterations.txt", 
			"3000_graph_iterations.txt", 
			"3000_tree_iterations.txt", 
			"3_tree_iterations.txt", 
			"40_tree_iterations.txt", 
			"45_iterations.txt", 
			"4torus_nv10k_reebgraph_vals.txt", 
			"4torus_simple_ReebGraph 2.txt", 
			"4torus_simple_ReebGraph.txt", 
			"5000_graph_iterations.txt", 
			"5000_tree_iterations.txt", 
			"500_graph_iterations.txt", 
			"500_tree_iterations.txt", 
			"5_iterations.txt", 
			"77_iterations.txt", 
			"80_tree_iterations.txt", 
			"buddha_10k-2_poission_f_ReebGraph.txt", 
			"buddha_10k_f_2_ReebGraph.txt", 
			"buddha_10k_f_3_ReebGraph.txt", 
			"buddha_10k_f_4_ReebGraph.txt", 
			"buddha_10k_f_5_ReebGraph.txt", 
			"buddha_10k_poission_f_ReebGraph.txt", 
			"buddha_10k_reebgraph_vals.txt", 
			"david_reebgraph_vals.txt", 
			"david_simple_ReebGraph.txt", 
			"eight_reebgraph_vals.txt", 
			"elevation_graph.txt", 
			"female_reebgraph_vals.txt", 
			"figure_eight_simple_ReebGraph.txt", 
			"first_graph.txt", 
			"first_graph10.txt", 
			"first_graph2.txt", 
			"first_graph3.txt", 
			"first_graph4.txt", 
			"first_graph5.txt", 
			"first_graph6.txt", 
			"first_graph7.txt", 
			"first_graph8.txt", 
			"first_graph9.txt", 
			"first_graph_old.txt",
			"running_example.txt", 
			"flower_poission_f_ReebGraph.txt", 
			"flower_reebgraph_vals.txt", 
			"flower_reebgraph.txt", 
			"greek_reebgraph_vals.txt", 
			"second_graph.txt", 
			"topology_f_ReebGraph.txt", 
			"topology_reebgraph_vals.txt", 
			"topology_simple_ReebGraph.txt", 
			"vase_poission_f_ReebGraph.txt"
	};
	
	
	public static void main( String[] args ) {

		for( String ip : testSet ) {
			try {
				int testCount = 10;
				
				double mergeTimer = 0;
				double ppTimer    = 0;
				int i = 0;
				TestResults result = null;
				for( ; i < testCount; i++ ) {
					result = TestResults.testPerformance( "test/"+ip, false );
					if( result == null ) {
						System.out.println( "test FAILED: " + ip );
						break;
					}
					if( result.ppTimer.getElapsedMilliseconds() < 50 ) testCount = 100;
					if( result.ppTimer.getElapsedMilliseconds() < 1 ) testCount = 1000;
					mergeTimer += result.mergeTimer.getElapsedMilliseconds();
					ppTimer += result.ppTimer.getElapsedMilliseconds();
				}
				if( i == testCount ) {
					
					TestResults.savePersistentDiagram( result.rgMP, "pd/" + ip );
					System.out.print( ip.split("\\.")[0].replace("_", "\\_") + " & & & " );
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

}


