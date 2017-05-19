package net.winroad.dubbo;

public interface AnnotationService {
	/** 
	 * 登录方法 
	 * @return 是否登录成功 
	 **/ 
	boolean login(String username, String password);	
}
