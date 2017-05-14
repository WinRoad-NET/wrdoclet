package net.winroad.wrdoclet;

import java.math.BigDecimal;

import net.winroad.wrdoclet.data.WRDoc;
import net.winroad.wrdoclet.utils.Logger;
import net.winroad.wrdoclet.utils.LoggerFactory;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;

/**
 * @author AdamsLee
 * 
 */
public class HtmlDoclet extends AbstractDoclet {
	private static AbstractDoclet docletImpl;
	private static Logger logger = LoggerFactory.getLogger(HtmlDoclet.class);
	private static boolean initialized = false;
	
	/**
	 * The "start" method as required by Javadoc.
	 * 
	 * @param root
	 *            the root of the documentation tree.
	 * @see com.sun.javadoc.RootDoc
	 * @return true if the doclet ran without encountering any errors.
	 */
	public static boolean start(RootDoc root) {
		try {
			try {
				if(!initialize()) {
					return false;
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			return docletImpl.start(docletImpl, root);
		} finally {
			docletImpl.resetConfiguration();
		}
	}

	@Override
	public Configuration configuration() {
		return docletImpl.configuration();
	}

	@Override
	public void generateWRDocFiles(RootDoc root, WRDoc wrDoc)
			throws Exception {
		docletImpl.generateWRDocFiles(root, wrDoc);
	}

	@Override
	protected void generatePackageFiles(ClassTree classtree) throws Exception {
		docletImpl.generatePackageFiles(classtree);
	}

	@Override
	protected void generateClassFiles(ClassDoc[] arr, ClassTree classtree) {
		docletImpl.generateClassFiles(arr, classtree);
	}

	public static boolean validOptions(String options[][],
			DocErrorReporter reporter) {
		return (AbstractConfiguration.getInstance())
				.validOptions(options, reporter);
	}

	private static boolean initialize() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		BigDecimal version = new BigDecimal(System.getProperty("java.specification.version"));		
		if(!initialized) {
			if(version.equals(new BigDecimal("1.7"))) {
				logger.info("loading HtmlDoclet for jdk1.7");
				ClassLoader classLoader = HtmlDoclet.class.getClassLoader();
				classLoader.loadClass("net.winroad.htmldoclet4jdk7.ConfigurationImpl");
				Class.forName("net.winroad.htmldoclet4jdk7.ConfigurationImpl");
				classLoader.loadClass("net.winroad.htmldoclet4jdk7.HtmlDoclet");	
				initialized = true;
				docletImpl = (AbstractDoclet) Class.forName("net.winroad.htmldoclet4jdk7.HtmlDoclet").newInstance();
			} else if(version.equals(new BigDecimal("1.8"))) {
				logger.info("loading HtmlDoclet for jdk1.8");
				ClassLoader classLoader = HtmlDoclet.class.getClassLoader();
				classLoader.loadClass("net.winroad.htmldoclet4jdk8.ConfigurationImpl");
				Class.forName("net.winroad.htmldoclet4jdk8.ConfigurationImpl");
				classLoader.loadClass("net.winroad.htmldoclet4jdk8.HtmlDoclet");	
				initialized = true;
				docletImpl = (AbstractDoclet) Class.forName("net.winroad.htmldoclet4jdk8.HtmlDoclet").newInstance();
			} else {
				logger.info("HtmlDoclet does not support jdk" + version);
			}
		}
		return initialized;
	}
	
	public static int optionLength(String option) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		initialize();
		return (AbstractConfiguration.getInstance()).optionLength(option);
	}

	@Override
	public void resetConfiguration() {
		docletImpl.resetConfiguration();		
	}
}
