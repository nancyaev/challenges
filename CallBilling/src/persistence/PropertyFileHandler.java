package persistence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
/**
 * This class handles the read of properties file
 * @author nestrada
 *
 */
public class PropertyFileHandler {
	
	/**
	 * Reads the property from the given file
	 * @param property
	 * @param file
	 * @return
	 */
	public static String readProperty(String property, String file){
		String propertyValue = null;
		FileInputStream inputStream;
		Properties propertiesFile = new Properties();
		
		try {
			inputStream = new FileInputStream(file);
			propertiesFile.load(inputStream);
			propertyValue = propertiesFile.getProperty(property);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return propertyValue;

	}

}
