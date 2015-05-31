package net.winroad.Service;

import java.util.HashMap;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import net.winroad.Models.User;

@WebService
public interface TestService {
	/*
	 * 获取用户列表
	 * 
	 * @wr.tag User
	 * 
	 * @author Adams
	 * 
	 * @version 0.0.1
	 * 
	 * @wr.memo init create
	 * 
	 * @author Bob
	 * 
	 * @version 0.0.2
	 * 
	 * @wr.memo fix bug
	 */
	@WebResult(name = "getUserResult")
	public List<User> getUserList(@WebParam(name = "name") String name);

	public int addUser(
			@WebParam(name = "username", partName = "stupidPartName") String name,
			String password);

	public void delUser(int userId);

	@WebResult(name = "getUserByIdResult")
	public User getUserById(int userId);

	public User[] getUserArray();

	public HashMap<Integer, String> getUserMap();
}
