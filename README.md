# wrdoclet
wrdoclet主要用来自动化生成接口文档，隐藏内部实现细节，只暴露调用方需要关心的接口细节。目前主要支持的是Spring MVC，SOAP服务，dubbo服务。因依赖于jdk的tools.jar包，工具目前只能在jdk1.7编译通过。jdk1.6存在严重bug http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6709246 因此无法支持。jdk1.8后续会考虑支持。
生成的接口文档示例请参见：http://winroad-net.github.io/wrdoclet/

This doclet tries to generate API doc for Spring MVC service, SOAP service, dubbo service.

supported Spring MVC service example:

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody
	Student addStudent(@RequestBody Student student) {
		return student;
	}	
	
supported SOAP service example:

	@WebResult(name = "getUserResult")
	public List<User> getUserList(@XmlElement(required=false) @WebParam(name = "name") String name);
	
supported dubbo service example:

	public interface DemoService {
		String sayHello(String name);
	}
	
	<dubbo:service interface="net.winroad.dubbo.DemoService" ref="demoService" />
