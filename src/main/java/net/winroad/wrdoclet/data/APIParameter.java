package net.winroad.wrdoclet.data;

public class APIParameter {
	private String name;
	private String description;
	private String required;
	private String type;
	private ParameterAppendHistory history;
	
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
	public String getRequired() {
		return required;
	}
	public void setRequired(String required) {
		this.required = required;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ParameterAppendHistory getHistory() {
		return history;
	}
	public void setHistory(ParameterAppendHistory history) {
		this.history = history;
	}
}
