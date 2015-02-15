package net.winroad.wrdoclet.utils;

import net.winroad.wrdoclet.utils.Logger;

import org.apache.maven.plugin.logging.Log;

public final class LoggerFactory {
	static Logger logger = new Logger();
	
    public static void setLog(Log log) {
		logger.setMvnLog(log);
    }
	
	public static Logger getLogger(@SuppressWarnings("rawtypes") Class clazz) {
		return getLogger(clazz.getName());
	}
	
	public static Logger getLogger(String name) {
		logger.setSlf4jLog(org.slf4j.LoggerFactory.getLogger(name));
		return logger;		
	}
}
