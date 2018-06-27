

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

import usf.saav.common.Timer;
import usf.saav.common.TimerAverage;
import usf.saav.common.TimerNanosecond;


public class MultipleTestCLI {

	public static String [] testSet = new String[] {
			"test/100_iterations.txt",
			"test/100_tree_iterations.txt",
			"test/10_tree_iterations.txt",
			"test/200_tree_iterations.txt",
			"test/25_iterations.txt",
			"test/3_tree_iterations.txt",
			"test/40_tree_iterations.txt",
			"test/45_iterations.txt",
			"test/4torus_simple_ReebGraph 2.txt",
			"test/4torus_simple_ReebGraph.txt",
			"test/5_iterations.txt",
			"test/77_iterations.txt",
			"test/80_tree_iterations.txt",
			"test/buddha_10k-2_poission_f_ReebGraph.txt",
			"test/buddha_10k_f_2_ReebGraph.txt",
			"test/buddha_10k_f_3_ReebGraph.txt",
			"test/buddha_10k_f_4_ReebGraph.txt",
			"test/buddha_10k_f_5_ReebGraph.txt",
			"test/buddha_10k_poission_f_ReebGraph.txt",
			"test/david_simple_ReebGraph.txt",
			"test/figure_eight_simple_ReebGraph.txt",
			"test/first_graph.txt",
			"test/first_graph10.txt",
			"test/first_graph2.txt",
			"test/first_graph3.txt",
			"test/first_graph4.txt",
			"test/first_graph5.txt",
			"test/first_graph6.txt",
			"test/first_graph7.txt",
			"test/first_graph8.txt",
			"test/first_graph9.txt",
			"test/first_graph_old.txt",
			"test/flower_poission_f_ReebGraph.txt",
			"test/second_graph.txt",
			"test/topology_f_ReebGraph.txt",
			"test/topology_simple_ReebGraph.txt",
			"test/vase_poission_f_ReebGraph.txt",
			"test/elevation_graph.txt"
	};
	
	
	public static void main( String[] args ) {

		for( String ip : testSet ) {
			try {
				
				Timer mergeTimer = new TimerAverage( new TimerNanosecond() );
				Timer ppTimer = new TimerAverage( new TimerNanosecond() );
				int i = 0;
				for( ; i < 1000; i++ ) {
					if( !SingleTestCLI.testPerformance( ip, mergeTimer, ppTimer, false ) ) {
						System.out.println( "test FAILED: " + ip );
						break;
					}
				}
				if( i == 1000 ) System.out.println( "test succeeded: " + ip + " -- merge_time: " + mergeTimer.getElapsedMilliseconds() + " pp_timer: " + ppTimer.getElapsedMilliseconds() );
	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}


