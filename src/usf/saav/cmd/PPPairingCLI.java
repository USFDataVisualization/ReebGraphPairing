package usf.saav.cmd;

import java.util.ArrayList;

import usf.saav.common.TimerNanosecond;
import usf.saav.topology.reebgraph.ReebGraph;
import usf.saav.topology.reebgraph.pairing.PropagateAndPair;

public class PPPairingCLI {
	public static void main(String[] args) {
		
		if( args.length == 0 ) {
			System.out.println("");
			System.out.println("   ###################################################################################");
			System.out.println("   Propagate and pair: A single-pass approach to critical point pairing in reeb graphs");
			System.out.println("   International Symposium on Visual Computing, Springer, Cham, 2019");
			System.out.println("   Junyi Tu, Mustafa Hajij, and Paul Rosen");
			System.out.println("");
			System.out.println("   Usage:");
			System.out.println("      > java -jar ReebGraphPairingPPP.jar <file1> <file2> ... <fileN>");
			System.out.println("");

		}
		else {
			for( String ip : args ) {
				try {
					System.out.println(ip);
					ArrayList<ReebGraph> rg = TestResults.runAlgo( ip, new PropagateAndPair(), new TimerNanosecond(), false );
					TestResults.printPersistentDiagramCSV( rg );
					System.out.println();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
