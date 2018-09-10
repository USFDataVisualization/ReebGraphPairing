package usf.saav.common.algorithm;

import java.util.HashSet;

public class ConnectedComponent<T> extends HashSet<T> {
	private static final long serialVersionUID = 3619344715219432888L;
	
	public ConnectedComponent( ){ }
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for( T e : this ){
			sb.append( e.toString() );
			sb.append( " " );
		}
		return sb.toString();
	}
}
