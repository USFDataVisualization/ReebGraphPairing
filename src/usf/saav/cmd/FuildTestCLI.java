

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


public class FuildTestCLI {


	
	
	public static void main( String[] args ) {
		
		float mTimeAvg = 0;
		float pTimeAvg = 0;
		float ivAvg = 0;
		float cvAvg = 0;
		float lpAvg = 0;
		int totalIter = 0;
		
		for(int iter = 1; iter <= 120; iter++ ) {
			String ip = "cons_mapper_";
			if( iter < 10 ) ip += "0";
			if( iter < 100 ) ip += "0";
			ip += iter + ".txt";
			
			try {
				int testCount = 10;
				
				double mergeTimer = 0;
				double ppTimer    = 0;
				int i = 0;
				TestResults result = null;
				for( ; i < testCount; i++ ) {
					result = TestResults.testPerformance( "fluid/"+ip, false );
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
					
					mTimeAvg += (mergeTimer/testCount);
					pTimeAvg += (ppTimer/testCount);
					ivAvg += result.initial_verts;
					cvAvg += result.conditioned_verts;
					lpAvg += result.loops;
					
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
					totalIter++;
				}
	
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.print( ip.split("\\.")[0].replace("_", "\\_") + " & & & " );
			System.out.print( (ivAvg/totalIter) + " & " );
			System.out.print( (cvAvg/totalIter) + " & ");
			System.out.print( (lpAvg/totalIter) + " & " );
			if( (mTimeAvg) > 0.1 )
				System.out.printf("%.2f & ", (mTimeAvg) );
			else
				System.out.printf("%.2e & ", (mTimeAvg) );
			if( (pTimeAvg) > 0.1 )
				System.out.printf("%.2f \\\\ ", (pTimeAvg) );
			else
				System.out.printf("%.2e \\\\ ", (pTimeAvg) );
			System.out.println("\n\\hline");			
		}
	}

}


