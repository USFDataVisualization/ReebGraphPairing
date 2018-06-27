package usf.saav.topology.reebgraph.pairing;

import usf.saav.topology.reebgraph.ReebGraph;

public interface Pairing {
	public void pair( ReebGraph g );

	public String getName();
}
