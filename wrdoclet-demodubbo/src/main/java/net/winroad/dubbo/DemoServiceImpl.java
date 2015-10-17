package net.winroad.dubbo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DemoServiceImpl implements DemoService {

	public String sayHello(String name) {
		return "Hello " + name;
	}

	public List<String> sayHello(HashMap<String, LinkedList<Integer>> map) {
		return null;
	}
	
}
