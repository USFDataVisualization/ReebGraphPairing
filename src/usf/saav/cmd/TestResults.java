package usf.saav.cmd;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;

import usf.saav.common.Timer;
import usf.saav.common.TimerMillisecond;
import usf.saav.common.TimerNanosecond;
import usf.saav.topology.TopoTreeNode.NodeType;
import usf.saav.topology.reebgraph.ReebGraph;
import usf.saav.topology.reebgraph.ReebGraphLoader;
import usf.saav.topology.reebgraph.ReebGraphVertex;
import usf.saav.topology.reebgraph.pairing.MergePairing;
import usf.saav.topology.reebgraph.pairing.Pairing;
import usf.saav.topology.reebgraph.pairing.PropagateAndPair;

public class TestResults {

	int loops;
	int initial_verts;
	int conditioned_verts;
	Timer mergeTimer=new TimerNanosecond(), ppTimer=new TimerNanosecond();
	ArrayList<ReebGraph> rgMP;
	ArrayList<ReebGraph> rgPP;
	
	private TestResults() { }
	
	public static TestResults testPerformance( String inputfile, boolean verbose ) throws Exception {

		TestResults ret= new TestResults();
		
		ret.initial_verts = ReebGraphLoader.load( inputfile, false, false, verbose ).get(0).size();
		
		ret.rgMP = runAlgo( inputfile, new MergePairing(), ret.mergeTimer, verbose );
		ret.rgPP = runAlgo( inputfile, new PropagateAndPair(), ret.ppTimer, verbose );
		
		ret.conditioned_verts = 0;
		for( ReebGraph r : ret.rgMP ) {
			ret.conditioned_verts += r.size();
		}
		ret.loops = countLoops(ret.rgMP);
		
		if( verbose ) System.out.println("\nCOMPARING GRAPHS");
		if( !compareDiagrams( ret.rgMP, ret.rgPP, verbose ) ) {
			if( verbose ) System.out.println("ERROR: Difference Found in pairings");
			return null;
		}
		if( verbose ) System.out.println();
		return ret;
		
	}
	
	public static void savePersistentDiagram( ArrayList<ReebGraph> rg0, String filename ) throws FileNotFoundException {
		ArrayList<ReebGraphVertex> verts0 = new ArrayList<ReebGraphVertex>();
		for( ReebGraph rg : rg0 ) { verts0.addAll( rg ); }
		
		PrintWriter pw = new PrintWriter(filename);
		for( ReebGraphVertex v : verts0 ) {
			ReebGraphVertex p = (ReebGraphVertex)v.getPartner();
			if( p != null && v.value() > p.value() ) continue;
			pw.println( v.getBirth() + " " + v.getDeath() );				
		}		
		pw.close();	
		
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
			//if( verbose && found ) System.out.println("  ok ==== " + v + " | " + v.getPartner() );
			if( !found )  System.out.println("  error == " + v + " | " + v.getPartner() );
			ret = ret && found;
		}
		return ret;
	}
	
	public static ArrayList<ReebGraph> runAlgo( String inputfile, Pairing pairing, Timer timer, boolean verbose ) throws Exception {
		Timer t = new TimerMillisecond();

		if( verbose ) System.out.println( );
		if( verbose ) System.out.println( pairing.getName() );
		
		t.start();
		ArrayList<ReebGraph> rm1 = ReebGraphLoader.load(inputfile,true,true,verbose);
		t.end();
		if( verbose ) System.out.println(" Load time: " + t.getElapsedMilliseconds() + "ms");
		if( verbose ) System.out.println(" Connected components: " + rm1.size());
		
		timer.start();
		for( ReebGraph ccRG : rm1 ) {
			pairing.pair(ccRG);
		}
		timer.end();

		if( verbose ) System.out.println(" Total Loops: " + countLoops( rm1 ) );
		if( verbose ) System.out.println(" " + pairing.getName() + " computation time: " + timer.getElapsedMilliseconds() + "ms\n");
		if( verbose ) System.out.println(" PERSISTENCE DIAGRAM");
		//if( verbose ) printPersistentDiagram(rm1);
		
		return rm1;
	}
	
	
	private static int countLoops(ArrayList<ReebGraph> rg0) {
		ArrayList<ReebGraphVertex> verts0 = new ArrayList<ReebGraphVertex>();
		for( ReebGraph rg : rg0 ) { verts0.addAll( rg ); }
		int ret = 0;
		for( ReebGraphVertex v : verts0 ) {
			if( v.isEssential() && v.getType()==NodeType.DOWNFORK ) {
				//System.out.println("   " + v + " " + v.getPartner() );
				ret++;
			}
		}
		
		return ret;
	}
	
	
}
