package usf.saav.topology.reebgraph;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;


public class ReebGraphLoader extends ReebGraph {

	private static final long serialVersionUID = 7889260039234787058L;

	public static ArrayList<ReebGraph> load(String inputReebGraph,  boolean splitConnComps, boolean condition, boolean showWarnings ) throws Exception {

		HashMap<Integer, ReebGraphVertex> rvmap = new HashMap<Integer, ReebGraphVertex>();
		BufferedReader reader = new BufferedReader(new FileReader(inputReebGraph));
		ReebGraph tmpRG = new ReebGraph();
		String s;
		
		while( (s = reader.readLine()) != null) {
			String[] r = s.split("\\s");
			if( r.length == 0 ) continue;
			if (r[0].trim().equals("v") == true) {			     

				if(r.length != 3) {
					reader.close();
					throw new Exception("ERROR: Invalid vertex input");
				}

				int    v  = Integer.parseInt(r[1].trim());
				float  fn = Float.parseFloat(r[2].trim());

				ReebGraphVertex newR = new ReebGraphVertex( fn, fn, v );
				tmpRG.add( newR );
				rvmap.put( v, newR );
				
			} 
			if (r[0].trim().equals("e") == true) {

				if(r.length != 3) {
					reader.close();
					throw new Exception("ERROR: Invalid edge input");
				}

				ReebGraphVertex v1 = rvmap.get(Integer.parseInt(r[1]));
				ReebGraphVertex v2 = rvmap.get(Integer.parseInt(r[2]));

				if( v1 == null || v2 == null ) {
					reader.close();
					throw new Exception("ERROR: Edge not found " + r[1] + " " + r[2]);
				}
				
				if( v1 == v2 ) {
					if( showWarnings ) System.err.println("WARNING: Self referenced edge (ignored) " + v1 + " " + v2 );
					continue;
				}

				v1.addNeighbor(v2);
				v2.addNeighbor(v1);
			}
		}

		reader.close();
		
		tmpRG.resetInternalIDs();
		tmpRG.resetInternalValues();
		
		if( splitConnComps ) {
			return extractConnectedGraphs( rvmap.values(), condition );
		}
		else {
			ArrayList<ReebGraph> ret = new ArrayList<ReebGraph>();
			if( condition )
				ret.add( condition( rvmap.values(), 0.05f ) );
			else
				ret.add( tmpRG );
			return ret;
		}

	}

	private static ArrayList<ReebGraph> extractConnectedGraphs( Collection<ReebGraphVertex> verts, boolean condition ){

		ArrayList<ReebGraph> ret = new ArrayList<ReebGraph>();

		HashSet<ReebGraphVertex> visited = new HashSet<ReebGraphVertex>();		
		for( ReebGraphVertex v : verts ) {
			if( !visited.contains(v) ){
				ArrayList<ReebGraphVertex> newGraph = new ArrayList<ReebGraphVertex>();
				dfs( newGraph, v, visited );
				if( condition )
					ret.add( condition( newGraph, 0.05f ) );
				else
					ret.add( new ReebGraph(newGraph) );
			}
		}

		return ret;
	}


	private static void dfs( ArrayList<ReebGraphVertex> newGraph, ReebGraphVertex curVertex, HashSet<ReebGraphVertex> visited ) {
		//Stack<ReebGraphVertex> proc = new ArrayList<ReebGraphVertex>();
		Deque<ReebGraphVertex> proc = new ArrayDeque<ReebGraphVertex>();
		proc.add(curVertex);
		while(!proc.isEmpty()) {
			//ReebGraphVertex tos = proc.poll();
			ReebGraphVertex tos = proc.pop();
			if( visited.contains(tos) ) continue;
			visited.add(tos);
			newGraph.add(tos);
			for( ReebGraphVertex n : tos.neighbors) {
				//if( !visited.contains(n) ) proc.add(n);
				if( !visited.contains(n) ) proc.push(n);
			}
		}
		
	}

	private static ReebGraph condition( Collection<ReebGraphVertex> verts, float epsilon_percent  ) {

		Queue<ReebGraphVertex> proc = new LinkedList<ReebGraphVertex>( verts );
		ReebGraph ret = new ReebGraph();
		
		while( !proc.isEmpty() ) {
			ReebGraphVertex rv = proc.poll();

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

			// non-critical node
			if( cntAbove==1 && cntBelow==1 ) {
				conditionNonCritical( rv );
				continue;
			}
			
			// double fork (upfork and downfork)
			if( cntAbove>=2 && cntBelow>=2 ) {
				conditionDoubleFork( rv, proc, aboveDif*epsilon_percent );
				continue;
			}			
			
			// saddle/max 
			if( cntAbove==0 && cntBelow>=2 ) {
				conditionSaddleMax( rv, proc, belowDif*epsilon_percent );
				continue;
			}					
			
			//saddle/min
			if( cntAbove>=2 && cntBelow==0 ) {
				conditionSaddleMin( rv, proc, aboveDif*epsilon_percent );
				continue;
			}		
			
			// downfork with more than 2 connections
			if( cntAbove==1 && cntBelow>2 ) {
				conditionMonkeyDownFork( rv, proc, belowDif*epsilon_percent );
				continue;
			}			
			
			// upfork with more than 2 connections
			if( cntBelow==1 && cntAbove>2 ) {
				conditionMonkeyUpFork( rv, proc, aboveDif*epsilon_percent );
				continue;
			}			
			
			if( cntAbove==1 && cntBelow==2 ) { ret.add( rv ); continue; }
			if( cntAbove==2 && cntBelow==1 ) { ret.add( rv ); continue; }
			if( cntAbove==0 && cntBelow==1 ) { ret.add( rv ); continue; }
			if( cntAbove==1 && cntBelow==0 ) { ret.add( rv ); continue; }
			
			System.out.println("WARNING: " + cntBelow + " " + cntAbove );
			
			ret.add( rv );
			
		}
		ret.resetInternalIDs();
		ret.resetInternalValues();
		return ret;

	}

	private static void conditionNonCritical( ReebGraphVertex rv ) {
		ReebGraphVertex v0 = rv.neighbors.get(0);
		ReebGraphVertex v1 = rv.neighbors.get(1);
		v0.neighbors.remove(rv);
		v1.neighbors.remove(rv);
		ReebGraphVertex.setNeighbors(v0,v1);
	}
	
	private static void conditionDoubleFork( ReebGraphVertex rv, Queue<ReebGraphVertex> proc, float diff ) {
		ReebGraphVertex newR0 = new ReebGraphVertex( rv.value(), rv.getRealValue(), rv.getGlobalID() );
		ReebGraphVertex newR1 = new ReebGraphVertex( rv.value()+diff, rv.getRealValue(), rv.getGlobalID() );
		ReebGraphVertex.setNeighbors(newR0,newR1);
		for( ReebGraphVertex n : rv.neighbors ) {
			n.neighbors.remove(rv);
			if(rv.value()<n.value()) {
				ReebGraphVertex.setNeighbors(n,newR1);
			}
			else {
				ReebGraphVertex.setNeighbors(n,newR0);
			}
		}
		proc.add(newR0);
		proc.add(newR1);
	}
	
	private static void conditionMonkeyUpFork( ReebGraphVertex rv, Queue<ReebGraphVertex> proc, float diff ) {
		ReebGraphVertex newR1 = new ReebGraphVertex( rv.value()+diff, rv.getRealValue(), rv.getGlobalID() );
		ReebGraphVertex newR0 = new ReebGraphVertex( rv.value(), rv.getRealValue(), rv.getGlobalID() );
		int rcnt = 0;
		ReebGraphVertex.setNeighbors(newR0,newR1);
		for( ReebGraphVertex n : rv.neighbors ) {
			n.neighbors.remove(rv);
			if(n.value()<rv.value()) 
				ReebGraphVertex.setNeighbors(newR0, n);
			else {
				if( rcnt == 0 )
					ReebGraphVertex.setNeighbors(newR0,n);
				else
					ReebGraphVertex.setNeighbors(newR1,n);
				rcnt++;
			}
		}
		proc.add(newR0);
		proc.add(newR1);		
	}
	
	private static void conditionMonkeyDownFork( ReebGraphVertex rv, Queue<ReebGraphVertex> proc, float diff ) {
		ReebGraphVertex newR1 = new ReebGraphVertex( rv.value()-diff, rv.getRealValue(), rv.getGlobalID() );
		ReebGraphVertex newR0 = new ReebGraphVertex( rv.value(), rv.getRealValue(), rv.getGlobalID() );
		int rcnt = 0;
		ReebGraphVertex.setNeighbors(newR0,newR1);
		for( ReebGraphVertex n : rv.neighbors ) {
			n.neighbors.remove(rv);
			if(n.value()>rv.value()) 
				ReebGraphVertex.setNeighbors(newR0, n);
			else {
				if( rcnt == 0 )
					ReebGraphVertex.setNeighbors(newR0,n);
				else
					ReebGraphVertex.setNeighbors(newR1,n);
				rcnt++;
			}
		}
		proc.add(newR0);
		proc.add(newR1);
	}
	
	private static void conditionSaddleMax( ReebGraphVertex rv, Queue<ReebGraphVertex> proc, float diff ) {
		ReebGraphVertex newR = new ReebGraphVertex( rv.value()+diff, rv.getRealValue(), rv.getGlobalID() );
		ReebGraphVertex.setNeighbors(rv,newR);
		proc.add(newR);
		proc.add(rv);
		
	}

	private static void conditionSaddleMin( ReebGraphVertex rv, Queue<ReebGraphVertex> proc, float diff ) {
		ReebGraphVertex newR = new ReebGraphVertex( rv.value()-diff, rv.getRealValue(), rv.getGlobalID() );
		ReebGraphVertex.setNeighbors(rv,newR);
		proc.add(newR);
		proc.add(rv);
	}

}


