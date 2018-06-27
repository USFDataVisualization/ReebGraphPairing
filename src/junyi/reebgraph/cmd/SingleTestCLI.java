

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

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Properties;

import junyi.reebgraph.pairing.conventional.ConventionalPairing;
import usf.saav.common.SystemX;
import usf.saav.common.Timer;
import usf.saav.common.TimerMillisecond;
import usf.saav.common.TimerNanosecond;
import usf.saav.topology.reebgraph.ReebGraph;
import usf.saav.topology.reebgraph.ReebGraphLoader;
import usf.saav.topology.reebgraph.ReebGraphVertex;
import usf.saav.topology.reebgraph.pairing.MergePairing;
import usf.saav.topology.reebgraph.pairing.Pairing;
import usf.saav.topology.reebgraph.pairing.PropagateAndPair;




public class SingleTestCLI {

	
	public static float norm_epsilon = 0.01f;

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
			
			System.out.println( "Input File: " + inputFile );
			
			if( saveGraphDot )
				saveGraph( inputFile, ConventionalPairing.tmp_directory, verbose );
			
			double mergeTotal = 0, ppTotal = 0;
			
			for( int i = 0; i < 1000; i++ ) {
				Timer mstTimer, mergeTimer, ppTimer;
				if( testPerformance( inputFile, (mstTimer=new TimerNanosecond()), (mergeTimer=new TimerNanosecond()), (ppTimer=new TimerNanosecond()), verbose ) ) {
					System.out.println( "test succeeded: " + inputFile + " -- mst_time: " + mstTimer.getElapsedMilliseconds() + " merge_time: " + mergeTimer.getElapsedMilliseconds() + " pp_timer: " + ppTimer.getElapsedMilliseconds() );
				}
				else {
					System.out.println( "test FAILED: " + inputFile );
				}
				mergeTotal += mergeTimer.getElapsedMilliseconds();
				ppTotal    += ppTimer.getElapsedMilliseconds();
			}
			
			System.out.println( "Average -- Merge: " + (mergeTotal/1000) + ", P&P: " + (ppTotal/1000) );
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Invalid input. Check the input.properties file");
			System.exit(0);
		}
	}

	public static void saveGraph( String inputfile, String outputDir, boolean verbose ) throws Exception {
		Timer t = new TimerMillisecond();
		t.start();
		ArrayList<ReebGraph> rm1 = ReebGraphLoader.load(inputfile,true,true);
		t.end();
		if( verbose ) System.out.println("  Load time: " + t.getElapsedMilliseconds() + "ms");
		
		t.start();
		for(int i = 0; i < rm1.size(); i++ ) {
			SystemX.writeStringToFile(rm1.get(i).toDot(), outputDir + "graph"+i+".dot" );
		}
		t.end();
		if( verbose ) System.out.println("  Save time: " + t.getElapsedMilliseconds() + "ms");		
	}
	
	public static ArrayList<ReebGraph> testAlgo( String inputfile, Pairing pairing, Timer timer, boolean verbose ) throws Exception {
		Timer t = new TimerMillisecond();

		if( verbose ) System.out.println( );
		if( verbose ) System.out.println( pairing.getName() );
		
		t.start();
		ArrayList<ReebGraph> rm1 = ReebGraphLoader.load(inputfile,true,true);
		t.end();
		if( verbose ) System.out.println(" Load time: " + t.getElapsedMilliseconds() + "ms");
		if( verbose ) System.out.println(" Connected components: " + rm1.size());
		
		timer.start();
		for( ReebGraph ccRG : rm1 ) {
			pairing.pair(ccRG);
		}
		timer.end();
		if( verbose ) System.out.println(" " + pairing.getName() + " computation time: " + timer.getElapsedMilliseconds() + "ms\n");
		if( verbose ) System.out.println(" PERSISTENCE DIAGRAM");
		if( verbose ) printPersistentDiagram(rm1);
		
		return rm1;
	}
	
	public static boolean testPerformance( String inputfile, Timer convTimer, Timer mergeTimer, Timer ppTimer, boolean verbose ) throws Exception {

		//ArrayList<ReebGraph> rn1 = testAlgo( inputfile, new ConventionalPairing(), convTimer, verbose );
		ArrayList<ReebGraph> rn2 = testAlgo( inputfile, new MergePairing(), mergeTimer, verbose );
		ArrayList<ReebGraph> rn3 = testAlgo( inputfile, new PropagateAndPair(), ppTimer, verbose );
		/*
		if( verbose ) System.out.println("\nCOMPARING GRAPHS (MST/Merge)");
		if( !compareDiagrams( rn1, rn2, verbose ) ) {
			if( verbose ) System.out.println("ERROR: Difference Found in Graph Pairings (mst/merge)");
			return false;
		}
		if( verbose ) System.out.println("\nCOMPARING GRAPHS (MST/P&P)");
		if( !compareDiagrams( rn1, rn3, verbose ) ) {
			if( verbose ) System.out.println("ERROR: Difference Found in Graph Pairings (mst/p&p)");
			return false;
		}
		*/
		if( verbose ) System.out.println("\nCOMPARING GRAPHS (Merge/P&P)");
		if( !compareDiagrams( rn2, rn3, verbose ) ) {
			if( verbose ) System.out.println("ERROR: Difference Found in Graph Pairings (merge/p&p)");
			return false;
		}
		if( verbose ) System.out.println();
		return true;
		
	}
	


	public static void printPersistentDiagram(ArrayList<ReebGraph> rg0) {
		ArrayList<ReebGraphVertex> verts0 = new ArrayList<ReebGraphVertex>();
		for( ReebGraph rg : rg0 ) { verts0.addAll( rg ); }
		
		verts0.sort( new Comparator<ReebGraphVertex>() {
			@Override
			public int compare(ReebGraphVertex o1, ReebGraphVertex o2) {
				if( o1.getBirth() < o2.getBirth() ) return -1;
				if( o1.getBirth() > o2.getBirth() ) return  1;
				if( o1.getDeath() < o2.getDeath() ) return -1;
				if( o1.getDeath() > o2.getDeath() ) return  1;
				return 0;
			}
		});
		
		for( ReebGraphVertex v : verts0 ) {
			ReebGraphVertex p = (ReebGraphVertex)v.getPartner();
			if( p == null ) {
				System.out.println("  [" + v.getRealValue() + ",INF) " + v.getGlobalID() + "/-1" );				
			}
			else {
				if( v.value() > p.value() ) continue;
				System.out.println("  [" + v.getRealValue() + "," + p.getRealValue() + ") " + v.getGlobalID() + "/" + p.getGlobalID() );
			}
		}				
	}
	
	
	public static boolean compareDiagrams( ArrayList<ReebGraph> rg0, ArrayList<ReebGraph> rg1, boolean verbose ) {
		
		ArrayList<ReebGraphVertex> verts0 = new ArrayList<ReebGraphVertex>();
		ArrayList<ReebGraphVertex> verts1 = new ArrayList<ReebGraphVertex>();
		for( ReebGraph rg : rg0 ) { verts0.addAll( rg ); }
		for( ReebGraph rg : rg1 ) { verts1.addAll( rg ); }
		
		verts0.sort( new Comparator<ReebGraphVertex>() {
			@Override
			public int compare(ReebGraphVertex o1, ReebGraphVertex o2) {
				if( o1.getBirth() < o2.getBirth() ) return -1;
				if( o1.getBirth() > o2.getBirth() ) return  1;
				if( o1.getDeath() < o2.getDeath() ) return -1;
				if( o1.getDeath() > o2.getDeath() ) return  1;
				return 0;
			}
		});
		
		boolean ret = true;
		for( ReebGraphVertex v : verts0 ) {
			ReebGraphVertex p = (ReebGraphVertex)v.getPartner();
			if( p == null ) {
				System.out.println("  error == " + v + " | NULL" );
				ret = false;
				continue;
			}
			if( v.value() > p.value() ) continue;
			boolean found = false;
			for( ReebGraphVertex o : verts1 ) {
				found = v.getGlobalID() == o.getGlobalID() && ((ReebGraphVertex)v.getPartner()).getGlobalID() == ((ReebGraphVertex)o.getPartner()).getGlobalID();
				if( found ) break;
			}
			if( verbose && found ) System.out.println("  ok ==== " + v + " | " + v.getPartner() );
			if( !found )  System.out.println("  error == " + v + " | " + v.getPartner() );
			ret = ret && found;
		}
		return ret;
	}
}


