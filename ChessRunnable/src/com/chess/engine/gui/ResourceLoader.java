package gui;
import java.io.InputStream;

/*
 * 
 *  The ResourceLoader allows the jar runnable to contain the resources
 *  or images to load when launching the jar. 
 * 
 */

final public class ResourceLoader {

	public static InputStream load(String path) {
		
		InputStream input = ResourceLoader.class.getResourceAsStream(path);
		if (input == null) {
			input = ResourceLoader.class.getResourceAsStream("/"+path);
			
		}
		return input;
	}
	
}
