package net.winroad.wrdoclet.taglets;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

public class WRRefReqTaglet implements Taglet {
	public static final String NAME = "refReq";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean inConstructor() {
		return false;
	}

	@Override
	public boolean inField() {
		return false;
	}

	@Override
	public boolean inMethod() {
		return false;
	}

	@Override
	public boolean inOverview() {
		return false;
	}

	@Override
	public boolean inPackage() {
		return false;
	}

	@Override
	public boolean inType() {
		return false;
	}

	@Override
	public boolean isInlineTag() {
		return false;
	}

	@Override
	public String toString(Tag tag) {
		return toString(new Tag[] { tag });
	}

	@Override
	public String toString(Tag[] tags) {
		return WRRefReqTaglet.concat(tags);
	}

	public static String concat(Tag[] tags) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < tags.length - 1; i++) {
			result.append(tags[i].text());
			result.append("/n");
		}
		if (tags.length > 0) {
			result.append(tags[tags.length - 1].text());
		}
		return result.toString();
	}

	/**
	 * Register this Taglet.
	 * 
	 * @param tagletMap
	 *            the map to register this tag to.
	 */
	@SuppressWarnings("unchecked")
	public static void register(@SuppressWarnings("rawtypes") Map tagletMap) {
		WRRefReqTaglet tag = new WRRefReqTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}

}
