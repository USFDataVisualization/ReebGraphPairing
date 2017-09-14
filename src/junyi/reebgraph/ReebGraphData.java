package src.junyi.reebgraph;

import src.junyi.reebgraph.loader.MeshLoader;

public class ReebGraphData {


private ReebGraph rg=new ReebGraph();	


public void loadData(MeshLoader loader) {
		try {
			int rowNo = loader.getRowCount();
			
			System.out.println(rowNo);
			
		
			pr("Finished reading data from file. Loading it......");
		
			pr("Successfully loaded Data");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}


private void pr(String s) {
	// TODO Auto-generated method stub
	System.out.println(s);
}


private void er(String s) {
	// TODO Auto-generated method stub
	System.out.println("Error!!");
	
	System.out.println(s);
	System.exit(1);
}


public ReebGraph getRg() {
	return rg;
}


public void setRg(ReebGraph rg) {
	this.rg = rg;
}



	
	
	
}	