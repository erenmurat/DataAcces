package com.ingbank;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class TimerProps {

	String result = "";
	InputStream inputStream;
	static String hostname ;
	static String ip ;
	static String port;
	static String fileName ;
 
	public String getPropValues() throws IOException {
 
		try {
			Properties prop = new Properties();
			String propFileName = "config.properties";
 
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
 
			Date time = new Date(System.currentTimeMillis());
 
			hostname = prop.getProperty("hostname");
			ip = prop.getProperty("ip");
			port = prop.getProperty("port");
			fileName =prop.getProperty("file");
 
 			
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
		return result;
	}
	
	
	
	
}
