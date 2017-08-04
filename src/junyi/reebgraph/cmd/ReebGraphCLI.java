

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
package src.junyi.reebgraph.cmd;



import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.Properties;


import src.junyi.reebgraph.ReebGraphData;
import src.junyi.reebgraph.ReebLoader2;


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
			//ReebGraph rg = new ReebGraph();	
			//rg.setGranularity(granularity);
			st = System.currentTimeMillis();
			
			ReebLoader2 loader=new ReebLoader2();
			loader.setInputFile(ip);
			//rg.computeReebGraph(loader, fn);
			
			
			ReebGraphData rgData=new ReebGraphData();
			rgData.loadData(loader);
			
			PrintStream prStrm = new PrintStream(new File(op));
			
			rgData.getRg().removeDeg2Nodes();
			
			
			rgData.getRg().outputReebGraph(prStrm);
			
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


