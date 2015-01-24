package net.winroad.wrdoclet.data;

public class OpenAPI {
	/*
	 * the description for this API
	 */
	private String description;
	private RequestMapping requestMapping;
	private APIModificationHistory modificationHistory;
	private APIParameter outParameter;
	private APIParameter inParameter;
	
	public APIModificationHistory getModificationHistory() {
		return modificationHistory;
	}

	public void setModificationHistory(
			APIModificationHistory modificationHistory) {
		this.modificationHistory = modificationHistory;
	}

	public APIParameter getOutParameter() {
		return outParameter;
	}

	public void setOutParameter(APIParameter outParameter) {
		this.outParameter = outParameter;
	}

	public APIParameter getInParameter() {
		return inParameter;
	}

	public void setInParameter(APIParameter inParameter) {
		this.inParameter = inParameter;
	}

	public RequestMapping getRequestMapping() {
		return requestMapping;
	}

	public void setRequestMapping(RequestMapping requestMapping) {
		this.requestMapping = requestMapping;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
