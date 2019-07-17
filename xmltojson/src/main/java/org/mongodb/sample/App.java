package org.mongodb.sample;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
    	
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
    	
    }
    
    static String getUsersHomeDir() {
	    String users_home = System.getProperty("user.home");
	    return users_home.replace("\\", "/"); // to support all platforms.
	}
}
