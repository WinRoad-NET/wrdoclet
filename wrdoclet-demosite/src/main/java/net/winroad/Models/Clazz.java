package net.winroad.Models;

import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class Clazz {

	/**
	 * 课程名称
	 * 
	 * @author Bob huang
	 * @version 0.0.2
	 * @memo fix bla bla bla bla bla bla bla bla bug
	 * @occurs required
	 */
	public String getName() {
		return name;
	}

	/**
	 * 课程名称
	 * 
	 * @author Bob zhong
	 * @version 0.0.2
	 * @memo fix bug
	 * @occurs required
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

	public List<Clazz> getRelatedClasses() {
		return relatedClasses;
	}

	public void setRelatedClasses(List<Clazz> relatedClasses) {
		this.relatedClasses = relatedClasses;
	}

	private String name;
	private List<Student> students;
	private List<Clazz> relatedClasses;
	@SuppressFBWarnings(value="UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD", justification = "just for demo")
	public Map.Entry<Address, List<Person>> properties;
}
