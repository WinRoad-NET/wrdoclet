package net.winroad.Service;

import java.util.HashMap;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.ResponseWrapper;

import net.winroad.Models.User;

/**
 * @tag demo
 * @author AdamsLi
 * Created by Adams at 2015.1.1
 */
@WebService
public interface TestService {
	/* 悲催，这种注释doclet不认
	 * 获取用户列表
	 * 
	 * @tag User
	 * 
	 * @author Adams
	 * 
	 * @version 0.0.1
	 * 
	 * @memo init create
	 * 
	 * @author Bob
	 * 
	 * @version 0.0.2
	 * 
	 * @memo fix bug
	 */
	/**
	 * 获取用户列表
	 * @param name 用户名称
	 * @tag 用户管理， 用户展示
	 * 
	 * @author Adams
	 * @version 0.0.1
	 * @memo init create
	 * 
	 * @author Bob
	 * @version 0.0.2
	 * @memo fix bug
	 * 
	 * @return 用户列表
	 */
	@WebResult(name = "getUserResult")
	public List<User> getUserList(@XmlElement(required=false) @WebParam(name = "name") String name);

	/**
	 * @tag 用户管理， 测试
	 * @param name 名字
	 * @param password 密码
	 * @return
	 */
	public int addUser(
			@WebParam(name = "username", partName = "stupidPartName") String name,
			@XmlElement(required=true) String password);

	public boolean findUser(@XmlElement(required=false) User user);
	
	/**
	 * @param user the user to update.
	 * @return whether user is updated.
	 */
	public boolean updateUser(@XmlElement(required=true) User user);
	
	public void delUser(int userId);
	
	@WebMethod(operationName = "delUserTest")
	public void delUser();
	
	@WebMethod(operationName = "GetUserByTheId", action = "http://www.winroad.net/GetUserByTheId")
	@WebResult(name = "getUserByIdResult")
	@ResponseWrapper(localName = "GetUserByTheIdResponse", targetNamespace = "http://www.winroad.net/")
	public User getUserById(int userId);

	public User[] getUserArray();

	public HashMap<Integer, String> getUserMap();
}
