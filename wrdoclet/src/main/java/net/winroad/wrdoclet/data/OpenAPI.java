package net.winroad.wrdoclet.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.sun.javadoc.Tag;

public class OpenAPI {
	/*
	 * the description for this API
	 */
	private String description;
	/*
	 * the brief description for this API
	 */
	private String brief;
	private RequestMapping requestMapping;
	private ModificationHistory modificationHistory;
	private APIParameter outParameter;
	private List<APIParameter> inParameters = new LinkedList<APIParameter>();
	private String qualifiedName;
	private Boolean authNeeded;
	private Set<String> tags = new HashSet<String>();
	
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

	public String getQualifiedName() {
		return qualifiedName;
	}

	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}

	public Boolean getAuthNeeded() {
		return authNeeded;
	}

	public void setAuthNeeded(Boolean authNeeded) {
		this.authNeeded = authNeeded;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}
	
	public void addTags(Tag[] tags) {
		for(Tag t : tags) {
			this.tags.add(t.text());
		}
	}
	
	public void addTags(Collection<String> tags) {
		for(String t : tags) {
			this.tags.add(t);
		}
	}
	
	public void addTag(String tag) {
		this.tags.add(tag);
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}
}
