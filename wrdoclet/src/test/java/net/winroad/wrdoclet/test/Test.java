package net.winroad.wrdoclet.test;

import java.io.File;

import net.winroad.wrdoclet.taglets.WRMemoTaglet;
import net.winroad.wrdoclet.taglets.WRReturnCodeTaglet;
import net.winroad.wrdoclet.taglets.WRTagTaglet;

public class Test {
	public static void main(String[] args) {
		String[] docArgs = new String[] {
				"-doclet",
				//net.winroad.wrdoclet.PdfDoclet.class.getName(),
				net.winroad.wrdoclet.HtmlDoclet.class.getName(),
				//"-noframe",
				"-docletpath",
				new File(System.getProperty("user.dir"), "target/classes")
						.getAbsolutePath(),
				"-taglet",
				WRTagTaglet.class.getName(),
				WRMemoTaglet.class.getName(),
				WRReturnCodeTaglet.class.getName(),
				"-tagletpath",
				new File(System.getProperty("user.dir"), "target/classes")
						.getAbsolutePath(),
				"-encoding",
				"utf-8",
				"-charset",
				"utf-8",
				//"-branchname",
				//"release",
				"-codeurl",
				"http://127.0.0.1/demobubbo/branches/2016.1.1release/",
				"-systemname",
				"demodubbo",
				"-searchengine",
				"http://127.0.0.1:8080/solr/apidocs",
				"-buildid",
				"003",
				//"-dubboconfigpath",
				//"D:/Git/wrdoclet/wrdoclet-demodubbo/src/main/resources/provider.xml",
				"-springcontextconfigpath",
				//"C:/Users/AdamsLi/Downloads/webApplicationContext.xml",	
				"D:/Git/wrdoclet/wrdoclet-demosite/src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml",
				//"-excludedurlsxpath",
				//"//:beans/mvc:interceptors/mvc:interceptor/:bean/:property/:list/:value",				
				"-sourcepath",
				//"D:/Git/wrdoclet/wrdoclet-demodubbo/src/main/java",
				//"D:/Git/wrdoclet/wrdoclet-demoservice/src/main/java",
				"D:/Git/wrdoclet/wrdoclet-demosite/src/main/java",
				"net.winroad.Controller",
				"net.winroad.Service",
				"net.winroad.Models",
				"net.winroad.dubbo",
				"org.springframework.web.bind.annotation",
				//"-nodeprecated",
				"-classpath",
				new File(
						System.getProperty("user.home"),
						".m2/repository/org/springframework/spring-web/3.2.3.RELEASE/spring-web-3.2.3.RELEASE.jar")
						.getAbsolutePath(),
				"-d",
				new File(System.getProperty("user.dir"), "target/doc")
						.getAbsolutePath() };
		com.sun.tools.javadoc.Main.execute(docArgs);

		System.out.println("Doc generated to: "
				+ new File(System.getProperty("user.dir"), "target/doc")
						.getAbsolutePath());
	}
}
