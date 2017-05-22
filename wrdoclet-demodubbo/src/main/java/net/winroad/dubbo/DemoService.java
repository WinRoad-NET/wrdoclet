package net.winroad.dubbo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public interface DemoService {
	/**
	 * 说Hello
	 * @param name 姓名
	 * @return 招呼
	 */
	String sayHello(String name);
	
	List<String> sayHello(HashMap<String, LinkedList<Integer>> map);
}