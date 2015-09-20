package net.winroad.wrdoclet.doc;

import java.util.LinkedList;
import java.util.List;

public class DocData {
	private List<Doc> docs = new LinkedList<Doc>();
	private FacetCounts facet_counts = new FacetCounts();

	public List<Doc> getDocs() {
		return docs;
	}

	public void setDocs(List<Doc> docs) {
		this.docs = docs;
	}
	
	public void addDoc(Doc doc) {
		this.docs.add(doc);
	}

	public FacetCounts getFacet_counts() {
		return facet_counts;
	}

	public void setFacet_counts(FacetCounts facet_counts) {
		this.facet_counts = facet_counts;
	}
}
