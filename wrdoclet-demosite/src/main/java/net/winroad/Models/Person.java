package net.winroad.Models;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * 人
 * @author AdamsLi
 * @version 0.1
 * @memo init create
 */
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
	private Address address;
	private List<String> friendNames;
	
	public final static String PET_PHRASE = "MY GOD!";
	@SuppressFBWarnings(value="SS_SHOULD_BE_STATIC", justification = "just for demo")
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

	@NotEmpty(message = "name should not be empty")
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
	@SuppressFBWarnings(value="URF_UNREAD_FIELD", justification = "just for demo")
	private void setW(double weight) {
		this.weight = weight;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
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

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<String> getFriendNames() {
		return friendNames;
	}

	public void setFriendNames(List<String> friendNames) {
		this.friendNames = friendNames;
	}
}
