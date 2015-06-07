package net.winroad.wrdoclet.data;

import java.util.LinkedList;
import java.util.List;

public class OpenAPI {
	/*
	 * the description for this API
	 */
	private String description;
	private RequestMapping requestMapping;
	private ModificationHistory modificationHistory;
	private APIParameter outParameter;
	private List<APIParameter> inParameters = new LinkedList<APIParameter>();
	
	/*
	 * Possible return code list.
	 */
	private String returnCode;

	/*
	 * @return Whether this API has modification on specified version. If no
	 * version specified, returns true.
	 */
	public boolean isModifiedOnVersion(String version) {
		if( this.modificationHistory.isModifiedOnVersion(version)
				|| this.outParameter.isModifiedOnVersion(version) ) {
			return true;
		}
		for(APIParameter inParameter : inParameters) {
			if(inParameter.isModifiedOnVersion(version)) {
				return true;
			}
		}
		return false;
	}

	public ModificationHistory getModificationHistory() {
		return modificationHistory;
	}

	public void setModificationHistory(ModificationHistory modificationHistory) {
		this.modificationHistory = modificationHistory;
	}

	public APIParameter getOutParameter() {
		return outParameter;
	}

	public void setOutParameter(APIParameter outParameter) {
		this.outParameter = outParameter;
	}

	public List<APIParameter> getInParameters() {
		return inParameters;
	}

	public boolean addInParameter(APIParameter inParameter) {
		return this.inParameters.add(inParameter);
	}

	public boolean addInParameters(List<APIParameter> inParameters) {
		return this.inParameters.addAll(inParameters);
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
