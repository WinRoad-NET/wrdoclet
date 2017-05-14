package net.winroad.wrdoclet.taglets;

import java.util.Map;

import com.sun.tools.doclets.Taglet;

public class WRReturnTaglet extends AbstractWRParamTaglet {
	public static final String NAME = "wr.return";

	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * Register this Taglet.
	 * 
	 * @param tagletMap
	 *            the map to register this tag to.
	 */
	@SuppressWarnings("unchecked")
	public static void register(@SuppressWarnings("rawtypes") Map tagletMap) {
		WRReturnTaglet tag = new WRReturnTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}
}
