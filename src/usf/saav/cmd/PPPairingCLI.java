package usf.saav.cmd;

import java.util.ArrayList;

import usf.saav.common.TimerNanosecond;
import usf.saav.topology.reebgraph.ReebGraph;
import usf.saav.topology.reebgraph.pairing.PropagateAndPair;

public class PPPairingCLI {
	public static void main(String[] args) {
		
		for( String ip : args ) {
			try {
				System.out.println(ip);
				ArrayList<ReebGraph> rg = TestResults.runAlgo( ip, new PropagateAndPair(), new TimerNanosecond(), false );
				TestResults.printPersistentDiagram( rg );
				System.out.println();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}