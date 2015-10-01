package net.winroad.wrdoclet.taglets;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

public class WRBriefTaglet implements Taglet {
	public static final String NAME = "wr.brief";
	
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
        for (int i = 0; i< tags.length - 1; i++) {
            result.append(tags[i].text());
            result.append("/n");
        }
        if(tags.length > 0) {
        	result.append(tags[tags.length - 1].text());
        }
        return result.toString();
    }

    /**
     * Register this Taglet.
     * @param tagletMap  the map to register this tag to.
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
