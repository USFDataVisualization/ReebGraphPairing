package usf.saav.topology.reebgraph;

public class Conditioner {

	/*
	
	public void printPersistentDiagram( ) {
		for(pairing p : buildPairings() ) {
			System.out.println( p );
		}
	}	
	
	
	private ArrayList<pairing> buildPairings(){
		ArrayList<pairing> pairs = new ArrayList<pairing>();

		for( int i = 0; i < rg.size(); i++ ) {
			ReebGraphVertex v1  = (ReebGraphVertex)rg.get(i);
			ReebGraphVertex v1p = (ReebGraphVertex)v1.getPartner();

			if( v1 == null || v1p == null ) {
				System.out.println(v1 + " " + v1p);
				continue;
			}

			v1  = denormailze(v1);
			v1p = denormailze(v1p);

			if( v1.getGlobalID() < v1p.getGlobalID() ) {
				pairs.add( new pairing(v1,v1p) );
			}
		}
		
		pairs.sort( new Comparator<pairing>() {
			@Override public int compare(pairing o1, pairing o2) {
				return o1.compareTo(o2);
			}			
		});		
		return pairs;
	}
	
	private class pairing implements Comparable<pairing>{
		ReebGraphVertex v1, v2;
		pairing( ReebGraphVertex _v1, ReebGraphVertex _v2){
			v1 = _v1;
			v2 = _v2;
		}
		@Override
		public int compareTo(pairing o) {
			if( v1.getBirth() < o.v1.getBirth() ) return -1;
			if( v1.getBirth() > o.v1.getBirth() ) return  1;
			if( v1.getDeath() < o.v1.getDeath() ) return -1;
			if( v1.getDeath() > o.v1.getDeath() ) return  1;
			return 0;
		}
		@Override
		public String toString() {
			return v1.getGlobalID() + " " + v2.getGlobalID() + " [" + v1.getBirth() + ", " + v1.getDeath() + ")" ;
		}
		public boolean equal( pairing o1 ) {
			if( v1.getGlobalID() != o1.v1.getGlobalID() ) return false;
			if( v2.getGlobalID() != o1.v2.getGlobalID() ) return false;
			if( v1.getBirth() != o1.v1.getBirth() ) return false;
			if( v1.getDeath() != o1.v1.getDeath() ) return false;
			return true;
		}
		
	}
	
	private void makeNeighbors(ReebGraphVertex v, Collection<ReebGraphVertex> neighbors) {
		for( ReebGraphVertex n : neighbors ) {
			makeNeighbors(v,n);
		}
	}


	private void makeNeighbors(ReebGraphVertex v0, ReebGraphVertex v1) {
		v0.addNeighbor(v1);
		v1.addNeighbor(v0);
	}

	
	public static boolean compareDiagrams( Conditioner n0, Conditioner n1, boolean verbose ) {
		
		ArrayList<pairing> p0 = n0.buildPairings();
		ArrayList<pairing> p1 = n1.buildPairings();
		
		boolean eq = p0.size() == p1.size();
		if( verbose && !eq ) { System.out.println("  error different sizes"); }
		for( int i = 0; i < Math.min(p0.size(), p1.size()); i++ ) {
			if( p0.get(i).equal( p1.get(i) ) ) {
				if( verbose ) System.out.print("  ok ==== ");
			}
			else {
				if( verbose ) System.out.print("  error == ");
				eq = false;
			}
			if( verbose ) System.out.println( p0.get(i) + " | " + p1.get(i) );
		}
		
		return eq;
	}
	*/
}
