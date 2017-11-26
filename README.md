# wrdoclet(问道)

**使用手册：http://wrdoclet.winroad.net/  ** :(有时需要翻墙)

wrdoclet是基于javadoc doclet的，用来自动化生成接口文档的工具。相比于javadoc自带的doclet，其隐藏了内部实现细节，只暴露调用方需要关心的接口细节。目前主要支持的服务框架是Spring MVC，JAX-WS的SOAP服务，dubbo服务。生成接口文档后还可以与solr、jenkins集成，实现文档搜索以及自动化发布的功能。

因依赖于jdk的tools.jar包，jdk 1.6存在严重bug http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6709246 因此无法支持。目前最新的版本已支持jdk 1.7和1.8。为支持不同版本的jdk，部分代码分拆到另外的独立项目中以方便编译。

jdk1.9 https://docs.oracle.com/javase/9/docs/api/jdk/javadoc/doclet/package-summary.html#migration 对doclet这块进行了大规模的重写。因此wrdoclet暂时无法支持jdk1.9。目前正在改造中，敬请期待。

wrdocletbase(https://github.com/WinRoad-NET/wrdocletbase) 是基础模块.
htmldoclet4jdk7(https://github.com/WinRoad-NET/htmldoclet4jdk7) 是基于wrdocletbase的针对jdk1.7的模块
htmldoclet4jdk8(https://github.com/WinRoad-NET/htmldoclet4jdk8) 是基于wrdocletbase的针对jdk1.8的模块
wrdoclet 依赖于以上3个模块，在运行时根据运行环境的jdk版本采用不同的模块来生成文档。

**生成的接口文档示例请参见：http://wrdocletdemo.winroad.net/ **

击链接加入群【问道】：https://jq.qq.com/?_wv=1027&k=5p3XY0m

This doclet tries to generate API doc for Spring MVC service, JAX-WS service, dubbo service.

supported Spring MVC service example:

	/**
	 * 添加一个学生
	 * @tag 学生管理
	 * @param student 要添加的学生
	 * 
	 * @author Adams 
	 * @version 0.0.1 
	 * @memo 添加接口
	 * 
	 * @author Bob 
	 * @version 0.0.2 
	 * @memo fix bug
	 * 
	 * @return 被添加的学生
	 * @returnCode 400 404 503
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody
	Student addStudent(@RequestBody Student student) {
		return student;
	}	
	
	public class Student extends Person {
		...
		
		/**
		 * @occurs required
		 * @param schoolName 学校名称
		 */
		public void setSchoolName(String schoolName) {
			this.schoolName = schoolName;
		}

		/**
		 * @author Bob
		 * @version 0.0.1
		 * @memo 添加学号字段
		 * 
		 * @author Adams
		 * @version 0.0.2
		 * @memo 修改为可选字段
		 * @occurs optional
		 * @param sno 学号
		 */
		public void setSno(String sno) {
			this.sno = sno;
		}

		private String schoolName;
		private String sno;
	}	
	
supported JAX-WS service example:

	/**
	 * 获取用户列表
	 * @param name 用户名称
	 * @tag 用户管理， 用户展示
	 * 
	 * @author Adams
	 * @version 0.0.1
	 * @memo init create
	 * 
	 * @author Bob
	 * @version 0.0.2
	 * @memo fix bug
	 * 
	 * @return 用户列表
	 */
	@WebResult(name = "getUserResult")
	public List<User> getUserList(@XmlElement(required=false) @WebParam(name = "name") String name);
	

	public class User {
		private String password;
		
		...
		
		/**
		 * @return 密码
		 */
		public String getPassword() {
			return password;
		}
	}	
	
	
supported dubbo service example:

	public interface DemoService {
		String sayHello(String name);
	}
	
	<dubbo:service interface="net.winroad.dubbo.DemoService" ref="demoService" />


利用maven-javadoc-plugin插件接入非常方便, 将下面配置添加到pom.xml中的<build><plugins>...</plugins></build>内即可。命令行执行 mvn javadoc:javadoc 即可开始生成接口文档。

	<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-javadoc-plugin</artifactId>
		<version>2.10.3</version>
		<configuration>
			<doclet>net.winroad.wrdoclet.HtmlDoclet</doclet>
			<docletArtifact>
				<groupId>net.winroad</groupId>
				<artifactId>wrdoclet</artifactId>
				<version>1.1.5</version>
			</docletArtifact>
			<useStandardDocletOptions>false</useStandardDocletOptions><!-- important ! -->
			<additionalparam>
				-systemname ${wrdoclet.systemname}
				-branchname ${wrdoclet.branchname}
				-encoding utf-8
				-charset utf-8
				-d
				../../../../../wrdoclet-gh-pages/apidocs-demoservice
			</additionalparam>
		</configuration>
	</plugin>
