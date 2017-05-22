package net.winroad.dubbo;

public interface AnnotationService {

	/**
	 * 登录方法 
	 * @param username 用户姓名
	 * @param password 密码
	 * @return 是否登录成功 
	 */
	boolean login(String username, String password);	
}
