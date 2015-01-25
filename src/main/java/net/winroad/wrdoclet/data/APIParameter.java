package net.winroad.wrdoclet.data;

public class APIParameter {
	private String name;
	private String description;
	private ParameterOccurs parameterOccurs;
	private String type;
	private ModificationHistory history;
	
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
}
