

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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import junyi.reebgraph.ReebGraph;
import junyi.reebgraph.ReebGraph.ReebGraphVertex;
import junyi.reebgraph.SystemXv2;
import junyi.reebgraph.pairing.conventional.ConventionalPairing;
import junyi.reebgraph.pairing.merge.MergePairing;




public class ReebGraphCLI {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Properties p = new Properties();
			System.out.println(new File(".").getAbsolutePath());
			p.load(new FileInputStream("input.properties"));
			//String loaderType = p.getProperty("loader");
			String ip = p.getProperty("inputFile").trim();
			String fn = p.getProperty("inputFunction").trim();
			if(!isInteger(fn)) {
				System.err.println("Input function should be a co-ordinate index (0 indicates given scalar function)");
				System.exit(1);
			}
			int granularity = Integer.parseInt(p.getProperty("granularity").trim());
			if(granularity != -1 && granularity < 1) {
				granularity = 1;
			}
			String op = null;
			try {
				 op = p.getProperty("output").trim();	
			} catch (Exception e) {
				op = null;
			}
			if(op != null && op.equalsIgnoreCase("")) {
				op = null;
			}
			
			long st, en;
			
			st = System.currentTimeMillis();
			
			System.out.println( ip );
			
			float norm_epsilon = 0.00001f;
			
			
			System.out.println("CONVENTIONAL");
			ReebGraph rm1 = new ReebGraphLoader(ip);
			
			SystemXv2.writeDot(rm1.toDot(), ConventionalPairing.tmp_directory + "graph.dot", ConventionalPairing.tmp_directory + "graph.pdf");
			rm1.Normalize( norm_epsilon );
			SystemXv2.writeDot(rm1.toDot(), ConventionalPairing.tmp_directory + "graph_norm.dot", ConventionalPairing.tmp_directory + "graph_norm.pdf");
			//int curCC = 0;
			for( ReebGraph rg : rm1.extractConnectedComponents() ) {
				//SystemXv2.writeDot(rg.toDot(), ConventionalPairing.tmp_directory + "cc" + curCC + ".dot", ConventionalPairing.tmp_directory + "cc" + curCC + ".pdf");
				new ConventionalPairing( rg );
				//curCC++;
			}
			rm1.printPD();
			
			
			System.out.println();
			System.out.println("OUR APPROACH");
			ReebGraph rm2 = (new ReebGraphLoader(ip)).Normalize( norm_epsilon );
			new MergePairing( rm2 );
			rm2.printPD();
			
			System.out.println();
			System.out.println("Comparing Graphs");
			boolean gequal = true;
			for( int i = 0; i < rm1.size(); i++ ) {
				ReebGraphVertex v1 = (ReebGraphVertex)rm1.get(i);
				ReebGraphVertex v2 = (ReebGraphVertex)rm2.get(i);
				if( v1.getBirth()==v2.getBirth() && v1.getDeath()==v2.getDeath() ) {
					System.out.print("  ok - ");
				}
				else {
					System.out.print("  ERROR - ");
					gequal = false;
				}
				System.out.println( v1 + " | " + v2 );
			}
			if( !gequal ) {
				System.out.println("ERROR: Difference Found in Graphs");
			}
	
			en = System.currentTimeMillis();
			System.out.println("Total Time Taken : " + (en - st) + "ms");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Invalid input. Check the input.properties file");
			System.exit(0);
		}
	}

	private static boolean isInteger(String fn) {
		try {
			Integer.parseInt(fn.trim());
			return true;
		} catch(Exception e) {
			return false;
		}
	}
}


