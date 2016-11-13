package net.winroad.beans;

import javax.validation.constraints.NotNull;

public class School {
	/**
	 * 学校名称
	 */
	@NotNull
	private String name;

	/**
	 * @return 学校名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name 学校名称
	 */
	public void setName(String name) {
		this.name = name;
	}
}
