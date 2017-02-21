package gr.auth.ee.mug.cfg.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * 
 * @author Vasileios Papapanagiotou
 */
public class LoggerText {

	private final String filename;
	private BufferedWriter bufferedWriter;
	
	public LoggerText(String filename) {
		this.filename = filename;
	}
	
	public void append(String s) throws Exception {
		bufferedWriter.write(s);
	}
	
	public void appendln(String s) throws Exception {
		bufferedWriter.write(s + "\n");
	}
	
	public void close() throws Exception {
		bufferedWriter.close();
	}
	
	public String getFilename() {
		return new String(filename);
	}
	
	public void open() throws Exception {
		bufferedWriter = new BufferedWriter(new FileWriter(filename));
	}
	
}
