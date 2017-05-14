package net.winroad.wrdoclet.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.winroad.wrdoclet.AbstractConfiguration;
import net.winroad.wrdoclet.builder.AbstractDocBuilder;
import net.winroad.wrdoclet.builder.DubboDocBuilder;
import net.winroad.wrdoclet.builder.RESTDocBuilder;
import net.winroad.wrdoclet.builder.SOAPDocBuilder;
import net.winroad.wrdoclet.utils.Logger;
import net.winroad.wrdoclet.utils.LoggerFactory;

import com.sun.tools.doclets.internal.toolkit.Configuration;

public class WRDoc {
	private Map<String, List<OpenAPI>> taggedOpenAPIs = new HashMap<String, List<OpenAPI>>();

	private List<AbstractDocBuilder> builders = new LinkedList<AbstractDocBuilder>();

	// The collection of tag name in this Doc.
	private Set<String> wrTags = new HashSet<String>();

	private Configuration configuration;

	private String docGeneratedDate;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Configuration getConfiguration() {
		return configuration;
	}

	public Set<String> getWRTags() {
		return this.wrTags;
	}

	public Map<String, List<OpenAPI>> getTaggedOpenAPIs() {
		return taggedOpenAPIs;
	}
	
	public String getDocGeneratedTime() {
		return this.docGeneratedDate;
	}

	public WRDoc(Configuration configuration) {
		this.configuration = configuration;
		Calendar c = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss"); 
		this.docGeneratedDate = df.format(c.getTime());
		this.builders.add(new RESTDocBuilder(this));
		this.logger.debug("RESTDocBuilder loaded.");
		this.builders.add(new SOAPDocBuilder(this));
		this.logger.debug("SOAPDocBuilder loaded.");
		String dubboConfigPath = ((AbstractConfiguration) this.configuration).dubboconfigpath;
		if (dubboConfigPath != null && !dubboConfigPath.isEmpty()) {
			this.builders.add(new DubboDocBuilder(this));
			this.logger.debug("DubboDocBuilder loaded with config path:" + dubboConfigPath);
		}
		this.build();
	}

	private void build() {
		for (AbstractDocBuilder builder : this.builders) {
			builder.buildWRDoc();
		}
	}
}
