package net.winroad.Models;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import net.winroad.beans.School;

/**
 * 学生
 * @author AdamsLi
 * @version 0.1
 * @memo init create
 */
public class Student extends Person {
	public String getSno() {
		return sno;
	}

	/**
	 * @author Bob
	 * @version 0.0.1
	 * @memo 添加学号字段
	 * 
	 * @author Adams
	 * @version 0.0.2
	 * @memo 修改为可选字段
	 * @occurs optional
	 * @param sno 学号
	 */
	public void setSno(String sno) {
		this.sno = sno;
	}

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	@NotNull
	@Valid
	private School school;
	@NotEmpty(message = "sno should not be empty")
	private String sno;
}
