package net.winroad.wrdoclet.data;

public class RequestMapping {
	private String url;
	private String methodType;
	//TODO: take "consumes"，"produces", "params"，"headers" into consideration.
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethodType() {
		return methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}
}
