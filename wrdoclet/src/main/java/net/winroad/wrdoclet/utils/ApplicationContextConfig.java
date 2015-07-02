package net.winroad.wrdoclet.utils;

import java.io.InputStream;
import java.util.Properties;

public class ApplicationContextConfig {
	static private String stopClasses = null;
	
	static {
		loadConfigFile();
	}

	synchronized static public void loadConfigFile() {
		if (stopClasses == null) {
			InputStream is = ApplicationContextConfig.class
					.getResourceAsStream("/wrdoclet.properties");
			Properties props = new Properties();
			try {
				props.load(is);
				stopClasses = props.getProperty("stop.classes");
			} catch (Exception e) {
				LoggerFactory.getLogger(ApplicationContextConfig.class).error(
						"不能读取属性文件. "
						+ "请确保wrdoclet.properties在CLASSPATH指定的路径中");
			}
		}
	}
	
	public static String getStopClasses() {
		if (stopClasses == null) {
			loadConfigFile();
		}
		return stopClasses;
	}
}
