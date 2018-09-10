package usf.saav.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Vector;

public class SystemX {
	
	public static void writeStringToFile( String str, String file ) throws IOException {
		PrintWriter pw = new PrintWriter(file);
		pw.append(str);
		pw.close();
	}
	
	public static String [] readFileContents( BufferedReader reader ) throws IOException {
		Vector<String> ret = new Vector<String>();
		 String line;
		 while( ( line = reader.readLine() ) != null ) {
	            ret.add(line);
	        }
		 reader.close();
		 return ret.toArray( new String[ret.size()] );
	}	


	public static String [] readFileContents( String filename ) throws IOException {
		return readFileContents( new BufferedReader(new FileReader( filename )) );
	}

	public static String [] readFileContents( URL filename ) throws IOException {
		return readFileContents( new BufferedReader( new InputStreamReader( filename.openStream() ) ) );
	}

	public static String [] readFileContents(File file) throws IOException {
		return readFileContents( new BufferedReader(new FileReader( file )) );
	}	
	
	public static String [] readFileContents(InputStream input) throws IOException {
		return SystemX.readFileContents( new BufferedReader(new InputStreamReader(input) ) );
	}
		

}
