package net.winroad.Service;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import net.winroad.Models.User;

@WebService
public interface TestService {
	public List<User> getUserList(@WebParam(name="name")String name);
}
