package junyi.reebgraph;
//package usf.saav.topology.join;

import java.io.BufferedReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import junyi.reebgraph.cmd.ReebMesh;
import junyi.reebgraph.cmd.ReebMesh.ReebVertex;
import junyi.reebgraph.cmd.ReebSpanningTree;
import junyi.reebgraph.loader.MeshLoader;
import usf.saav.mesh.Mesh.Vertex;
import usf.saav.topology.TopoTreeNode;
import usf.saav.topology.merge.MergeTree;
import usf.saav.topology.split.SplitTree;


public class ReebLoader2 implements MeshLoader{

	private BufferedReader reader;
	private int noNodes=0;
	private int noArcs=0;

	String inputReebGraph;


	private ReebMesh reebMesh = new ReebMesh();
	//private paulReebMesh reebMeshoneasdf = new paulReebMesh();

	// Connected components
	ArrayList<ReebMesh> conn_comp = new ArrayList<ReebMesh>();  

	// all Vertices
	ArrayList<ReebVertex> rv = new ArrayList<ReebVertex>();  //maybe multiple components


	//	public HashMap<Integer, ReebVertex> rvmap = new HashMap<Integer, ReebVertex>();

	public void setInputFile(String _inputReebGraph) {
		inputReebGraph = _inputReebGraph;

		HashMap<Integer, ReebVertex> rvmap = new HashMap<Integer, ReebVertex>();

		try {
			reader = new BufferedReader(new FileReader(inputReebGraph));
			String s = reader.readLine();

			String[] r = s.split("\\s");

			while(s != null) {
				r = s.split("\\s");
				if (r[0].trim().equals("v") == true) {			     

					noNodes++;
					// System.out.println(s);
					int v;
					float  fn;

					v = Integer.parseInt(r[1].trim());

					fn = Float.parseFloat(r[2].trim());


					ReebVertex reebV= reebMesh.createVertex(fn, v);

					//System.out.println(v + " <==> " + reebV.id() );

					rvmap.put(v, reebV);

					rv.add(reebV);

					s = reader.readLine();

				} 
				if (r[0].trim().equals("e") == true) {
					noArcs ++;

					int v1 = -1;
					int v2 = -1;

					//	System.out.println(s);

					if(r.length == 3) {
						v1 = Integer.parseInt(r[1]);
						v2 = Integer.parseInt(r[2]);

					} else {
						System.err.println("Invalid input");
						System.exit(0);
					}

					rvmap.get(v1).addNeighbor(rvmap.get(v2));
					rvmap.get(v2).addNeighbor(rvmap.get(v1));

					//	 System.out.println("neighbor# = " + rvmap.get(v1).neighbors().length); 
					//     System.out.println("neighbor# = " + rvmap.get(v2).neighbors().length);
					s = reader.readLine();
				}
			}


			System.out.println("Vertex =+++++++++++++++++++ "); 
			// printVertices(rv);

			ourPairing();

			System.out.println("No. of Nodes : " + noNodes);
			System.out.println("No. of Arcs : " + noArcs);


		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}


	public void conventionalPairing() {	


		MergeTree mt = new MergeTree(reebMesh );
		mt.run();


		TopoTreeNode gmax =null, gmin=null;
		for(int i = 0; i < mt.size(); i++ ){
			TopoTreeNode x = mt.getNode(i);
			reebMesh.getByID( x.getPosition() ).essent = false;
			if( x.getPartner() == null ){ 
				gmin = x; 
			}
			else{
				reebMesh.getByID( x.getPosition() ).topoPartner = reebMesh.getByID( x.getPartner().getPosition() );
			}
		}

		System.out.println(mt.toDot());


		SplitTree st = new SplitTree(reebMesh );
		st.run();
		System.out.println(st.toDot());
		for(int i = 0; i < st.size(); i++ ){
			TopoTreeNode x = st.getNode(i);
			reebMesh.getByID( x.getPosition() ).essent = false;
			if( x.getPartner() == null ){ 
				gmax = x; 
			}
			else{
				System.out.println( x.getPosition() + " " + x.getPartner().getPosition() );
			}
		}

		reebMesh.getByID( gmin.getPosition() ).topoPartner = reebMesh.getByID( gmax.getPosition() );
		reebMesh.getByID( gmax.getPosition() ).topoPartner = reebMesh.getByID( gmin.getPosition() );

		System.out.println(reebMesh.toDot());

		System.out.println();
		//	System.out.println(reebMeshIndex2Id.toDot());
		System.out.println();


		Vector<ReebVertex> ess = new Vector<ReebVertex>();
		//System.out.println(x.getPosition());
		for(int j = 0; j < reebMesh.size(); j++ ){
			if( ((ReebVertex)reebMesh.get(j)).essent ){
				ess.add( ((ReebVertex)reebMesh.get(j)) );
			}
		}


		for( ReebVertex r : ess ){

			if( r.isdownfork() ) {
				System.out.println();
				System.out.print("DOWNFORK -- ");
				System.out.println( r.toString() + "   " + r.isdownfork( ) );
				ReebSpanningTree pairing = new ReebSpanningTree(reebMesh, r);

				pairing.getUpFork().topoPartner = pairing.getDownFork();
				pairing.getDownFork().topoPartner = pairing.getUpFork();
				System.out.println(pairing);
			}
		}

		for( Vertex v : reebMesh ){
			ReebVertex rv = (ReebVertex)v;
			System.out.println( rv.globalID() + ": [" + rv.getBirth() + ", " + rv.getDeath() + "]");
		}

		//print_info_message( "Building tree complete" );
	}


	

	public void ourPairing() {

		Vector<ReebVertex> sortedNodes = new Vector<ReebVertex>();
		
		for( Vertex v : reebMesh ) {
			sortedNodes.add((ReebVertex)v);
		}
		
		sortedNodes.sort( new Comparator<ReebVertex>() {
			public int compare(ReebVertex o1, ReebVertex o2) {
				if( o1.value() < o2.value() ) return -1;
				if( o1.value() > o2.value() ) return  1;
				return 0;
			}
		});
		
		
		for( ReebVertex v : sortedNodes ) {
			Vector<ReebVertex> outlist = new Vector<ReebVertex>();
			
			if( v.isdownfork() ) {
				System.out.println("down: " + v.value());
				processDownFork(v,outlist);
			}
			else if( v.islocalmax() ) {
				System.out.println("local max: " + v.value());
				
				ReebVertex maxSaddle = null;
				for( ReebVertex n : v.in0 ) {
					if( n.topoPartner == null ) {
						maxSaddle = n;
					}
				}
				
				v.topoPartner = maxSaddle;
				maxSaddle.topoPartner = v;
			}
			else {
				if( v.in0 != null ) {
					for(ReebVertex n : v.in0 ) {
						if( n.topoPartner == null )
							outlist.add(n);
					}
				}
				outlist.add(v);
			}
			for( ReebVertex n : v.neighbors ) {
				if( n.value() > v.value() ) {
					if( n.in0 == null ) n.in0 = outlist;
					else n.in1 = outlist;
				}
			}
			
			System.out.println( v.value() + " " + outlist );
			
		}
		
		
		for( Vertex v : reebMesh ){
			ReebVertex rv = (ReebVertex)v;
			System.out.println( rv.globalID() + ": [" + rv.getBirth() + ", " + rv.getDeath() + "]");
		}
	}


	private void processDownFork(ReebVertex v, Vector<ReebVertex> outlist ) {

		int i = 0, j = 0;
		
		ReebVertex maxPair = null;
		ReebVertex maxMin  = null;
		while( i < v.in0.size() && j < v.in1.size() ) {
			if( v.in0.get(i).value() < v.in1.get(j).value() ) {
				if( v.in0.get(i).islocalmin() ) maxMin = v.in0.get(i);
				i++;
			}
			else if( v.in0.get(i).value() > v.in1.get(j).value() ) {
				if( v.in1.get(j).islocalmin() ) maxMin = v.in1.get(j);
				j++;
			}
			else {
				maxPair = v.in0.get(i);
				i++;
				j++;
			}
		}
		
		ReebVertex partner;
		
		if( maxPair != null ) {
			partner = maxPair;
		}
		else {
			partner = maxMin;
		}
			
		v.topoPartner = partner;
		partner.topoPartner = v;
		
		
		i = 0; j = 0;
		while( i < v.in0.size() && j < v.in1.size() ) {
			if( v.in0.get(i).value() < v.in1.get(j).value() ) {
				if( v.in0.get(i).topoPartner == null )
					outlist.add( v.in0.get(i) );
				i++;
			}
			else if( v.in0.get(i).value() > v.in1.get(j).value() ) {
				if( v.in1.get(j).topoPartner == null )
					outlist.add( v.in1.get(j) );
				j++;
			}
			else {
				if( v.in0.get(i).topoPartner == null ) {
					outlist.add( v.in0.get(i) );
				}
				i++;
				j++;
			}
		}
		while( i < v.in0.size() ) {
			if( v.in0.get(i).topoPartner == null )
				outlist.add( v.in0.get(i) );
			i++;
		}
		while( j < v.in1.size() ) {
			if( v.in1.get(j).topoPartner == null )
				outlist.add( v.in1.get(j) );
			j++;
		}
	}

	





}