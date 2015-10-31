package net.winroad.Models;

public class User {
	private int id;
	private String name;
	private String password;
	
	public User(){};
	
	public User(int id,String name,String password){
		this.id = id;
		this.name = name;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	/**
	 * 姓名
	 * @param name 名字
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return 密码
	 */
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
