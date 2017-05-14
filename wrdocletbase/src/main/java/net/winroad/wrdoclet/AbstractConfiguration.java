package net.winroad.wrdoclet;

import com.sun.tools.doclets.internal.toolkit.Configuration;

public abstract class AbstractConfiguration extends Configuration {

	/**
	 * Argument for command line option "-dubboconfigpath".
	 */
	public String dubboconfigpath = "";

	/**
	 * Argument for command line option "-springcontextconfigpath".
	 */
	public String springcontextconfigpath = "";

	/**
	 * Argument for command line option "-excludedurlsxpath".
	 */
	public String excludedurlsxpath = "";

	/**
	 * Argument for command line option "-branchname".
	 */
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
	public String branchname = "";

	/**
	 * Argument for command line option "-systemname".
	 */
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
	public String systemname = "";

	/**
	 * Argument for command line option "-codeurl".
	 */
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
	public String codeurl = "";

	/**
	 * Argument for command line option "-searchengine".
	 */
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
	public String searchengine = "http://127.0.0.1:8080/solr/apidocs";

	/**
	 * Argument for command line option "-buildid".
	 */
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
	public String buildid = "";

	@edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_SHOULD_BE_FINAL")
	protected static AbstractConfiguration instance;
	
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
	public static AbstractConfiguration getInstance() {
		return instance;
	}	
}
