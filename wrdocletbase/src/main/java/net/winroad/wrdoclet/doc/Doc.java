package net.winroad.wrdoclet.doc;

import java.util.LinkedList;
import java.util.List;

public class Doc {
	private String tag;
	private List<API> APIs = new LinkedList<API>();

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<API> getAPIs() {
		return APIs;
	}

	public void setAPIs(List<API> aPIs) {
		APIs = aPIs;
	}
}
