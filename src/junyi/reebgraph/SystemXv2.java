package junyi.reebgraph;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import usf.saav.common.SystemX;

public class SystemXv2 extends SystemX {
	
	public static String dot_path = "/opt/local/bin/dot";
	
	public static void writeDot( String dot, String dot_file, String pdf_file ) throws IOException {
		PrintWriter pw = new PrintWriter(dot_file);
		pw.append(dot);
		pw.close();
		String cmd = dot_path + " -Tpdf " + dot_file;
		System.out.println(cmd);
		//executeCommand(cmd,pdf_file);
	}
	
	

	public static void executeCommand(String command, String outfile ) {

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			FileOutputStream os = new FileOutputStream(outfile);
			
			int cur;
			while( (cur=reader.read()) != -1 ) {
				os.write(cur);
			}
			os.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
