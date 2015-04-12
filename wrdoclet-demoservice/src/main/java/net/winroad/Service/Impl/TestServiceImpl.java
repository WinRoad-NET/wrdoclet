package net.winroad.Service.Impl;
import java.util.Arrays;
import java.util.List;

import net.winroad.Models.User;
import net.winroad.Service.TestService;
import javax.jws.WebParam;
import javax.jws.WebService;
@WebService
public class TestServiceImpl implements TestService {

	List<User> list = Arrays.asList(new User(1,"admin","admin"),
									new User(2,"root","root"),
									new User(3,"test","test"),
									new User(4,"demo","demo"),
									new User());
	
	public List<User> getUserList(@WebParam(name="name")String name) {
		for(User u:list){
			u.setName(name);
		}
		return list;
	}
}
