/*
 *	Copyright (C) 2017 Visualization & Graphics Lab (VGL), USF
 *
 */
package junyi.reebgraph.loader;


/**
 * 
 * This interface should be extended in order to write a custom loader to support different input file formats.
 *   
 */
public interface MeshLoader {
	
	/**
	 * This method is first called to set the input. You need to perform the required initialization
	 * in this method.
	 * 
	 * @param inputMesh File name of the input mesh
	 */
	public void setInputFile(String inputMesh);
	
	/**
	 * This method is called after the setInputFile() method.
	 * 
	 * @return	The number of vertices in the input. Should be a positive number
	 */
	public int getRowCount(); 
	
	
	
	/**
	 * This method is called resets the file pointer to the beginning of the input file. 
	 */
	public void reset(); 
	
	
}
