package org.mongodb.sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This will generate a 1m rows Large XML
 * 
 * @author ksoong
 *
 */
public class GenerateLargeXML {
	
	static String TEMPLATE = new String("<row id=\"${REPLACE}\"><c1>20171010</c1><c2>1</c2><c3>20171009</c3><c4>20171011</c4><c5>20171010</c5><c6>20171010</c6><c7>20140406</c7><c8>19861209</c8><c9>20171012</c9><c10>20171012</c10><c11>O</c11><c12>20171010</c12><c14>2017283</c14><c15>NORMAL</c15><c16>20171009</c16><c17>20110107</c17><c20>LOCAL.PAYMENT.DAY NOT &gt; NEXT.WORKING.DAY}</c20><c20 m=\"2\">LOCAL.DISPO.DAY NOT &gt;NEXT.WORKING.DAY}</c20><c20 m=\"3\">DIFF. DATE FIELD &amp; /TODAY = &amp; DAYS}{7}-1261</c20><c22>2279</c22><c23>10085_COBUSER</c23><c24>1710092155</c24><c25>10085_COBUSER</c25><c26>${REPLACE}</c26><c27>1</c27></row>");

	public static void main(String[] args) throws Exception {
		
		String tmpDir = getUsersHomeDir() + File.separator + "tmp" ;
    	Path tmpPath = Paths.get(tmpDir);
    	if(!Files.exists(tmpPath)) {
    		Files.createDirectory(tmpPath);
    	}
    	
    	Path path = Paths.get(tmpPath.toString(), "rows.xml");
    	
    	if(!Files.exists(path)) {
    		Files.createFile(path);
    		System.out.println("Generate large xml to " + path);
    	}
    	
		FileOutputStream fos = new FileOutputStream(path.toFile());
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write("<rows>");
		bw.newLine();
		
		long start = System.currentTimeMillis();
		
		int base = 1000000;
		
		for(int i = 0 ; i < 1000000 ; i++) {
			String id = "CN" + Integer.toString(base + i);
			bw.write(TEMPLATE.replace("${REPLACE}", id));
			bw.newLine();
		}
		
		bw.write("</rows>");
		
		bw.close();
		
		System.out.println("Generate 1m rows spend " + (System.currentTimeMillis() - start) + " milliseconds");
	}
	
	static String getUsersHomeDir() {
	    String users_home = System.getProperty("user.home");
	    return users_home.replace("\\", "/"); // to support all platforms.
	}

}
