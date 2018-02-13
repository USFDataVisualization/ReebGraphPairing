package usf.saav.common;

import java.io.IOException;
import java.io.PrintWriter;

public class SystemX {
	
	public static void writeStringToFile( String str, String file ) throws IOException {
		PrintWriter pw = new PrintWriter(file);
		pw.append(str);
		pw.close();
	}

}
