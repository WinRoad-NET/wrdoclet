package net.winroad.wrdoclet.data;

public class RequestMapping {
	private String url;
	private String methodType;
	private String tooltip;
	private String containerName;
	
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

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
}
