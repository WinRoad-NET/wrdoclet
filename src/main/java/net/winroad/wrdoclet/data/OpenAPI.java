package net.winroad.wrdoclet.data;

public class OpenAPI {
	/*
	 * the description for this API
	 */
	private String description;
	private RequestMapping requestMapping;
	private ModificationHistory modificationHistory;
	private APIParameter outParameter;
	private APIParameter inParameter;
	/*
	 * Possible return code list.
	 */
	private String returnCode;
	
	public ModificationHistory getModificationHistory() {
		return modificationHistory;
	}

	public void setModificationHistory(
			ModificationHistory modificationHistory) {
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

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
}
