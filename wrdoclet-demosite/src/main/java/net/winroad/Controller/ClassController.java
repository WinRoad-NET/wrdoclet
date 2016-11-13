package net.winroad.Controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import net.winroad.Models.Clazz;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @tag Class
 * Created by Adams at 2014.1.1
 * @author AdamsLi
 * @version 0.1
 * @memo init create
 */
@Controller
@Deprecated
public class ClassController extends BaseController {
	// 悲催，这种注释doclet不认
	// 添加课程
	// @tag Clazz, School, OPS
	// @author Adams
	// @version 0.0.1
	// @memo init create
	// @author Bob
	// @version 0.0.2
	// @memo fix bug
	@RequestMapping(value = "/class/add", method = RequestMethod.POST)
	public @ResponseBody
	Clazz addClass(@RequestBody @NotNull Clazz clazz, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		return clazz;
	}

	/**
	 * 
	 添加课程列表. 添加
	 完后返回添加结果。
	 @tag Clazz；School，OPS 测试
	 @author Adams
	 @version 0.0.1
	 @memo init create
	 @author Bob
	 @version 0.0.2
	 @memo fix bug
	 */
	@RequestMapping(value = "/class/addlist", method = RequestMethod.POST)
	public @ResponseBody
	boolean addClassList(@RequestBody @NotEmpty @Valid List<Clazz> clazz, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		return true;
	}

	/**
	 * 删除课程 
	 * @tag Class, School
	 * @author Adams 
	 * @version 0.0.1 
	 * @memo init add api 
	 * @author Bob 
	 * @version 0.0.2 
	 * @memo fix bug
	 * @returnCode 400 404 503
	 */
	@RequestMapping(value = "/class/del", method = RequestMethod.POST)
	public @ResponseBody
	Clazz delClass(@RequestBody @NotNull Clazz clazz, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		return clazz;
	}
	
	/**
	 * @brief list课程 
	 * @tag Class 
	 * @author Adams 
	 * @version 0.0.1 
	 * @memo init add api 
	 * @author Bob 
	 * @version 0.0.2 
	 * @memo fix bug
	 * @returnCode 400 404 503
	 * @return the class list.
	 */
	@RequestMapping(value = {"/class/list","/class/listclass"}, method = RequestMethod.GET)
	public @ResponseBody
	List<Clazz> listClass(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		LinkedList<Clazz> result = new LinkedList<Clazz>();
		result.add(new Clazz());
		return result;
	}
	
	/**
	 * 自路由用例
	 * @api /class/foo
	 * @refReq body net.winroad.Models.Clazz
	 * @refResp net.winroad.Models.Address
	 */
	public void foo(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		
	}
	
}
