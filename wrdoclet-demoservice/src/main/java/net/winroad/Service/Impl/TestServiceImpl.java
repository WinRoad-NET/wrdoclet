package net.winroad.Service.Impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.winroad.Models.User;
import net.winroad.Service.TestService;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

@WebService
public class TestServiceImpl implements TestService {

	List<User> list = Arrays.asList(new User(1, "admin", "admin"), new User(2,
			"root", "root"), new User(3, "test", "test"), new User(4, "demo",
			"demo"), new User());

	public List<User> getUserList(@WebParam(name = "name") String name) {
		for (User u : list) {
			u.setName(name);
		}
		return list;
	}

	public boolean findUser(@XmlElement(required=false) User user) {
		return false;
	}
	
	public boolean updateUser(@XmlElement(required=true) User user) {
		return true;
	}
	
	@WebResult(name = "addResult")
	public int addUser(String name, String pwd) {
		return 110;
	}

	public void delUser(int id) {

	}

	public void delUser() {

	}
	
	public User getUserById(int userId) {
		return list.get(userId);
	}

	public User[] getUserArray() {
		return (User[]) list.toArray();
	}

	public HashMap<Integer, String> getUserMap() {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		for (User u : list) {
			map.put(u.getId(), u.getName());
		}
		return map;
	}
}
