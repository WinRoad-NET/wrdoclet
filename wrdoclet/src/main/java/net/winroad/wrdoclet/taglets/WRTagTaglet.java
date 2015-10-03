package net.winroad.wrdoclet.taglets;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

public class WRTagTaglet implements Taglet {
	public static final String NAME = "wr.tag";
	private static final String SPLITTER = ",";

	public String getName() {
		return NAME;
	}

	public boolean inConstructor() {
		return false;
	}

	public boolean inField() {
		return false;
	}

	public boolean inMethod() {
		return true;
	}

	public boolean inOverview() {
		return false;
	}

	public boolean inPackage() {
		return false;
	}

	public boolean inType() {
		return false;
	}

	public boolean isInlineTag() {
		return false;
	}

	public String toString(Tag tag) {
		return toString(new Tag[] { tag });
	}

	public String toString(Tag[] tags) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < tags.length - 1; i++) {
			result.append(tags[i].text());
			result.append(SPLITTER);
		}
		if (tags.length > 0) {
			result.append(tags[tags.length - 1].text());
		}
		return result.toString();
	}

	/**
	 * Split string to get tag set.
	 * 
	 * @param tags
	 *            the String of tags to split.
	 * @return set of tags.
	 */
	public static Set<String> getTagSet(String tags) {
		String[] tagArr = tags.split(SPLITTER);
		Set<String> tagSet = new HashSet<String>();
		for (int i = 0; i < tagArr.length; i++) {
			String tag = tagArr[i].trim();
			if (!tag.isEmpty()) {
				tagSet.add(tag);
			}
		}
		return tagSet;
	}

	/**
	 * Register this Taglet.
	 * 
	 * @param tagletMap
	 *            the map to register this tag to.
	 */
	@SuppressWarnings("unchecked")
	public static void register(@SuppressWarnings("rawtypes") Map tagletMap) {
		WRTagTaglet tag = new WRTagTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}
}
