package housing;

import java.util.Properties;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;

public class PropertyReader {
	//private static String path = "config.properties";
	private Properties prop = new Properties();

	public PropertyReader(String path) {
		InputStream input = null;

		try {
			input = new FileInputStream(path);
			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public String get(String var) {
		return(prop.getProperty(var));
	}
}

