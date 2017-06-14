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
				//"D:/winroad/wrdoclet-demo/wrdoclet-demodubbo/src/main/resources/provider.xml",
				"-springcontextconfigpath",
				//"C:/Users/AdamsLi/Downloads/webApplicationContext.xml",	
				"D:/winroad/wrdoclet-demo/wrdoclet-demosite/src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml",
				//"-excludedurlsxpath",
				//"//:beans/mvc:interceptors/mvc:interceptor/:bean/:property/:list/:value",				
				"-sourcepath",
				//"D:/winroad/wrdoclet-demo/wrdoclet-demodubbo/src/main/java;D:/winroad/wrdoclet-demo/wrdoclet-demodubbostub/src/main/java",
				//"D:/winroad/wrdoclet-demo/wrdoclet-demoservice/src/main/java",
				"D:/winroad/wrdoclet-demo/wrdoclet-demosite/src/main/java;D:/winroad/wrdoclet-demo/wrdoclet-beans/src/main/java",
				"net.winroad.Controller",
				"net.winroad.Service",
				"net.winroad.Models",
				"net.winroad.dubbo",
				"org.springframework.web.bind.annotation",
				"org.hibernate.validator.constraints",
				"javax.validation.constraints",
				"lombok",
				"com.alibaba.dubbo.config.annotation",
				"net.winroad.beans",
				//"-nodeprecated",
				"-classpath",
				new File(
						System.getProperty("user.home"),
						".m2/repository/org/springframework/spring-web/4.3.5.RELEASE/spring-web-4.3.5.RELEASE.jar")
						.getAbsolutePath().toString() + ";" +
				new File(
						System.getProperty("user.home"),
						".m2/repository/org/springframework/spring-webmvc/4.3.5.RELEASE/spring-webmvc-4.3.5.RELEASE.jar")
						.getAbsolutePath().toString() + ";" +
				new File(
						System.getProperty("user.home"),
						".m2/repository/org/springframework/spring-core/4.3.5.RELEASE/spring-core-4.3.5.RELEASE.jar")
						.getAbsolutePath().toString() + ";" +
				new File(
						System.getProperty("user.home"),
						".m2/repository/org/springframework/spring-context/4.3.5.RELEASE/spring-context-4.3.5.RELEASE.jar")
						.getAbsolutePath().toString() + ";" +
				new File(
						System.getProperty("user.home"),
						".m2/repository/com/fasterxml/jackson/core/jackson-core/2.8.6/jackson-core-2.8.6.jar")
						.getAbsolutePath().toString() + ";" +						
				new File(
						System.getProperty("user.home"),
						".m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.8.6/jackson-annotations-2.8.6.jar")
						.getAbsolutePath().toString() + ";" +						
				new File(
						System.getProperty("user.home"),
						".m2/repository/com/fasterxml/jackson/core/jackson-databind/2.8.6/jackson-databind-2.8.6.jar")
						.getAbsolutePath().toString() + ";" +						
				new File(
						System.getProperty("user.home"),
						".m2/repository/com/google/code/findbugs/findbugs/3.0.1/findbugs-3.0.1.jar")
						.getAbsolutePath().toString() + ";" +						
				new File(
						System.getProperty("user.home"),
						".m2/repository/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar")
						.getAbsolutePath().toString() + ";" +
				new File(
						System.getProperty("user.home"),
						".m2/repository/javax/validation/validation-api/1.1.0.Final/validation-api-1.1.0.Final.jar")
						.getAbsolutePath().toString() + ";" +
				new File(
						System.getProperty("user.home"),
						".m2/repository/org/hibernate/hibernate-validator/5.3.2.Final/hibernate-validator-5.3.2.Final.jar")
						.getAbsolutePath().toString() + ";" +
				new File(
						System.getProperty("user.home"),
						".m2/repository/org/projectlombok/lombok/1.16.16/lombok-1.16.16.jar")
						.getAbsolutePath().toString() + ";" +
				new File(
						System.getProperty("user.home"),
						".m2/repository/com/alibaba/dubbo/2.5.3/dubbo-2.5.3.jar")
						.getAbsolutePath().toString(),
				"-d",
				new File(System.getProperty("user.dir"), "target/doc")
						.getAbsolutePath() };
		com.sun.tools.javadoc.Main.execute(docArgs);

		System.out.println("Doc generated to: "
				+ new File(System.getProperty("user.dir"), "target/doc")
						.getAbsolutePath());
	}
}
