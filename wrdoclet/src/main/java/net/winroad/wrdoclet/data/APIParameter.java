package net.winroad.wrdoclet.data;

import java.util.LinkedList;
import java.util.List;

/*
 * It could be interface argument or field of object which should be listed in 
 * the document.
 */
public class APIParameter {
	private String name;
	private String description;
	private ParameterOccurs parameterOccurs;
	private String type;
	private ModificationHistory history;
	private List<APIParameter> fields;
	/**
	 * whether it's type argument for parent (the parent is generic type).
	 */
	private boolean isParentTypeArgument;

	/*
	 * @return Whether this parameter has modification on specified version. If
	 * no version specified, returns true.
	 */
	public boolean isModifiedOnVersion(String version) {
		if (version == null || version.isEmpty()) {
			return true;
		}
		if (this.history != null && this.history.isModifiedOnVersion(version)) {
			return true;
		}
		if (this.fields != null) {
			for (APIParameter param : this.fields) {
				if (param.isModifiedOnVersion(version)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ParameterOccurs getParameterOccurs() {
		return parameterOccurs;
	}

	public void setParameterOccurs(ParameterOccurs parameterOccurs) {
		this.parameterOccurs = parameterOccurs;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ModificationHistory getHistory() {
		return history;
	}

	public void setHistory(ModificationHistory history) {
		this.history = history;
	}

	public List<APIParameter> getFields() {
		return fields;
	}

	public void setFields(List<APIParameter> fields) {
		this.fields = fields;
	}

	public void appendField(APIParameter field) {
		if (this.fields == null) {
			this.fields = new LinkedList<APIParameter>();
		}

		this.fields.add(field);
	}

	public boolean isParentTypeArgument() {
		return isParentTypeArgument;
	}

	public void setParentTypeArgument(boolean isParentTypeArgument) {
		this.isParentTypeArgument = isParentTypeArgument;
	}
}
