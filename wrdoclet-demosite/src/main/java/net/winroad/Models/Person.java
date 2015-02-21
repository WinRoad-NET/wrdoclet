package net.winroad.Models;

public class Person {
	private String name;
	private int age;
	@SuppressWarnings("unused")
	private double weight;
	private Gender gender;
	private double h;
	private boolean isAdult;
	private String LOGOURL;
	private int xIndex;
	
	public final static String PET_PHRASE = "MY GOD!";
	public final String hobby = "hot girl";
	public static String favStar = "Jackie Chen";

	public boolean isAdult() {
		return isAdult;
	}

	public void setAdult(boolean isAdult) {
		this.isAdult = isAdult;
	}
	
	public String getTEast() {
		return this.name;
	}

	public void setTest(String test) {
		this.name = test;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@SuppressWarnings("unused")
	private void setW(double weight) {
		this.weight = weight;
	}

	public Gender getGender() {
		return gender;
	}

	public String saySomething() {
		return PET_PHRASE;
	}

	public void setHeight(double height) {
		this.h = height;
	}

	public double getHeight() {
		return h;
	}
	
	public String getHobbies() {
		return this.hobby;
	}

	public String getLOGOURL() {
		return LOGOURL;
	}

	public void setLOGOURL(String lOGOURL) {
		LOGOURL = lOGOURL;
	}

	public int getxIndex() {
		return xIndex;
	}

	public void setxIndex(int xIndex) {
		this.xIndex = xIndex;
	}
}
