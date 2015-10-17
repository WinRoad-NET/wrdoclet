package net.winroad.dubbo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public interface DemoService {
	String sayHello(String name);
	
	List<String> sayHello(HashMap<String, LinkedList<Integer>> map);
}