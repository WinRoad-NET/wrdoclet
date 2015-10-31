package net.winroad.Models;

/**
 * 学生
 * @author AdamsLi
 * @version 0.1
 * @memo init create
 */
public class Student extends Person {
	public String getSchoolName() {
		return schoolName;
	}

	/**
	 * @occurs required
	 * @param schoolName 学校名称
	 */
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

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

	private String schoolName;
	private String sno;
}
