package net.winroad.wrdoclet.doc;

import java.util.LinkedList;
import java.util.List;

public class FacetField {
	private List<Object> tags = new LinkedList<Object>();

	public List<Object> getTags() {
		return tags;
	}

	public void setTags(List<Object> tags) {
		this.tags = tags;
	}
	
	public void addTagField(String tagName, int tagCount) {
		this.tags.add(tagName);
		this.tags.add(tagCount);
	}
}
