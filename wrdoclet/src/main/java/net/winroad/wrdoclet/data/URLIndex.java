package net.winroad.wrdoclet.data;

public class URLIndex implements Comparable<Object> {
	private String index;
	private String filename;

	public URLIndex(String index, String filename) {
		this.index = index;
		this.filename = filename;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int compareTo(Object paramT) {
		return this.index.compareTo(((URLIndex) paramT).index);
	}

	@Override
	public String toString() {
		return this.index + " --> " + this.filename;
	}

}
