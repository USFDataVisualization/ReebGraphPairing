package junyi.reebgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import usf.saav.topology.TopoGraph;
import usf.saav.topology.reebgraph.ReebGraph;
import usf.saav.topology.reebgraph.ReebGraphVertex;

public class ReebGraphNormalizer {
	
	ReebGraph rg;
	HashMap< ReebGraphVertex, ReebGraphVertex > vmap = new HashMap< ReebGraphVertex, ReebGraphVertex >();
	

	public ReebGraphNormalizer( ReebGraph _rg, float epsilon_percent ) {
			rg = _rg;
			
			Queue<TopoGraph.Vertex> proc = new LinkedList<TopoGraph.Vertex>();
			proc.addAll(rg);
			
			while( !proc.isEmpty() ) {
				ReebGraphVertex rv = (ReebGraphVertex)proc.poll();
						
				int cntAbove=0;
				int cntBelow=0;
				float aboveDif = Float.MAX_VALUE;
				float belowDif = Float.MAX_VALUE;
				for( ReebGraphVertex n : rv.neighbors ) {
					if(rv.value()<n.value()) {
						cntAbove++;
						aboveDif = Math.min(aboveDif,n.value()-rv.value());
					}
					if(rv.value()>n.value()) {
						cntBelow++;
						belowDif = Math.min(belowDif,rv.value()-n.value());
					}
				}

				ArrayList<ReebGraphVertex> n0 = new ArrayList<ReebGraphVertex>();
				ArrayList<ReebGraphVertex> n1 = new ArrayList<ReebGraphVertex>();
				ReebGraphVertex newR = null;
						
				// mixed upfork and downfork
				if( cntAbove>=2 && cntBelow>=2 ) {
					newR = rg.createVertex( rv.value()+aboveDif*epsilon_percent, rg.getMaxGlobalID()+1 );
					n0.add(newR);
					for( ReebGraphVertex n : rv.neighbors ) {
						if(rv.value()<n.value()) {
							n1.add(n);
						}
						else {
							n0.add(n);
						}
						n.neighbors.remove(rv);
					}
				}
				
				// downfork with more than 2 connections
				if( cntAbove==1 && cntBelow>2 ) {
					newR = rg.createVertex( rv.value()-belowDif*epsilon_percent, rg.getMaxGlobalID()+1 );
					int rcnt = 0;
					n0.add(newR);
					for( ReebGraphVertex n : rv.neighbors ) {
						if(rv.value()>n.value()) {
							if( rcnt == 0 )
								n0.add(n);
							else
								n1.add(n);
							rcnt++;
						}
						else {
							n0.add(n);
						}
						n.neighbors.remove(rv);
					}
				}		
				
				// upfork with more than 2 connections
				if( cntAbove>2 && cntBelow==1 ) {
					newR = rg.createVertex( rv.value()+aboveDif*epsilon_percent, rg.getMaxGlobalID()+1 );
					int rcnt = 0;
					n0.add(newR);
					for( ReebGraphVertex n : rv.neighbors ) {
						if(rv.value()<n.value()) {
							if( rcnt == 0 )
								n0.add(n);
							else
								n1.add(n);
							rcnt++;
						}
						else {
							n0.add(n);
						}
						n.neighbors.remove(rv);
					}
				}	

				
				if( newR != null ) {
					addToVMap( newR, rv );
					rv.neighbors.clear();
					makeNeighbors(rv,n0);
					makeNeighbors(newR,n1);
					proc.add(rv);
					proc.add(newR);
				}
				
				if( cntAbove>=2 && cntBelow==0 || cntAbove==0 && cntBelow>=2 ) {
					if( cntAbove>=2 && cntBelow==0 ) newR = rg.createVertex( rv.value()-aboveDif*epsilon_percent, rg.getMaxGlobalID()+1 );
					if( cntAbove==0 && cntBelow>=2 ) newR = rg.createVertex( rv.value()+belowDif*epsilon_percent, rg.getMaxGlobalID()+1 );
					addToVMap( newR, rv );
					rv.neighbors.add(newR);
					newR.neighbors.add(rv);
					proc.add(rv);
					continue;
				}		
				
				// non-critical node
				if( cntAbove==1 && cntBelow==1 ) {
					ReebGraphVertex v0 = rv.neighbors.get(0);
					ReebGraphVertex v1 = rv.neighbors.get(1);
					v0.neighbors.remove(rv);
					v1.neighbors.remove(rv);
					v0.neighbors.add(v1);
					v1.neighbors.add(v0);
					rg.remove(rv);
					continue;
				}			
				
				
			}

			rg.resetInternalIDs();
			
			
			//for( Entry<ReebGraphVertex, ReebGraphVertex> e : vmap.entrySet() ) {
			//	System.out.println( e.getKey().getGlobalID() + "->" + e.getValue().getGlobalID() );
			//}
	}
	
	private void addToVMap( ReebGraphVertex primV, ReebGraphVertex origV ) {
		while( vmap.containsKey(origV) ) { origV = vmap.get(origV); }
		vmap.put( primV,  origV );
	}
	
	public ReebGraphVertex denormailze( ReebGraphVertex v ) {
		if( vmap.containsKey(v) ) return vmap.get(v);
		return v;
	}
	
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

	
	public static boolean compareDiagrams( ReebGraphNormalizer n0, ReebGraphNormalizer n1, boolean verbose ) {
		
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
}
