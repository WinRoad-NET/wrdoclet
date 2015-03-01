package net.winroad.Models;

import java.util.List;
import java.util.Map;

public class Clazz {

	/**
	 * 课程名称
	 * 
	 * @author Bob huang
	 * @version 0.0.2
	 * @wr.memo fix bla bla bla bla bla bla bla bla bug
	 * @wr.occurs required
	 */
	public String getName() {
		return name;
	}

	/**
	 * 课程名称
	 * 
	 * @author Bob zhong
	 * @version 0.0.2
	 * @wr.memo fix bug
	 * @wr.occurs required
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**选择该门功课的学生列表。
	 * @return
	 */
	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	private String name;
	private List<Student> students;
	public Map.Entry<Address, Person> properties;
}
