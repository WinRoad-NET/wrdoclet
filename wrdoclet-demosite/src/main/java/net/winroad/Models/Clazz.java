package net.winroad.Models;

public class Clazz {

	/**
	 * 课程名称
	 * 
	 * @author Bob
	 * @version 0.0.2
	 * @wr.memo fix bug
	 * @wr.occurs required
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @wr.occurs optional
	 */
	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	private String name;
	private String teacherName;
}
