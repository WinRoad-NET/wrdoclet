package net.winroad.dubbo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
	public static void main(String[] args) throws Exception {
		System.setProperty("dubbo.application.logger","slf4j");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "provider.xml" });
		context.start();
		System.out.println("请按任意键退出");
		System.in.read();
		context.close();
		System.out.println("已退出");
	}
}