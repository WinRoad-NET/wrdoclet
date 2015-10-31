# wrdoclet
wrdoclet主要用来自动化生成接口文档，隐藏内部实现细节，只暴露调用方需要关心的接口细节。目前主要支持的是Spring MVC，SOAP服务，dubbo服务。因依赖于jdk的tools.jar包，工具目前只能在jdk1.7编译通过。jdk1.6存在严重bug http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6709246 因此无法支持。jdk1.8后续会考虑支持。
生成的接口文档示例请参见：http://winroad-net.github.io/wrdoclet/

This doclet tries to generate API doc for Spring MVC service, SOAP service, dubbo service.

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
	
supported SOAP service example:

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

