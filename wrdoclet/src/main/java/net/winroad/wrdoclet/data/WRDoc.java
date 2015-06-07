package net.winroad.wrdoclet.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.tools.doclets.internal.toolkit.Configuration;

public class WRDoc {
	private Map<String, List<OpenAPI>> taggedOpenAPIs = new HashMap<String, List<OpenAPI>>();

	private List<AbstractDocBuilder> builders = new LinkedList<AbstractDocBuilder>();

	// The collection of tag name in this Doc.
	private Set<String> wrTags = new HashSet<String>();

	private Configuration configuration;

	public Configuration getConfiguration() {
		return configuration;
	}

	public Set<String> getWRTags() {
		return this.wrTags;
	}

	public Map<String, List<OpenAPI>> getTaggedOpenAPIs() {
		return taggedOpenAPIs;
	}

	public WRDoc(Configuration configuration) {
		this.configuration = configuration;
		this.builders.add(new RESTDocBuilder(this));
		this.builders.add(new SOAPDocBuilder(this));
		this.build();
	}

	private void build() {
		for (AbstractDocBuilder builder : this.builders) {
			builder.buildWRDoc();
		}
	}
}
