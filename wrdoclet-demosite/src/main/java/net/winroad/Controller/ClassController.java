package net.winroad.Controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.winroad.Models.Clazz;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ClassController extends BaseController {
	// 添加课程
	// @wr.tag Clazz, School, OPS
	// @author Adams
	// @version 0.0.1
	// @wr.memo init create
	// @author Bob
	// @version 0.0.2
	// @wr.memo fix bug
	@RequestMapping(value = "/class/add", method = RequestMethod.POST)
	public @ResponseBody
	Clazz addClass(@RequestBody Clazz clazz, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		return clazz;
	}

	/**
	 * 
	 添加课程列表. 添加
	 完后返回添加结果。
	 @wr.tag Clazz, School, OPS
	 @author Adams
	 @version 0.0.1
	 @wr.memo init create
	 @author Bob
	 @version 0.0.2
	 @wr.memo fix bug
	 */
	@RequestMapping(value = "/class/addlist", method = RequestMethod.POST)
	public @ResponseBody
	boolean addClassList(@RequestBody List<Clazz> clazz, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		return true;
	}

	/**
	 * 删除课程 
	 * @wr.tag Class, School
	 * @author Adams 
	 * @version 0.0.1 
	 * @wr.memo init add api 
	 * @author Bob 
	 * @version 0.0.2 
	 * @wr.memo fix bug
	 * @wr.returnCode 400 404 503
	 */
	@RequestMapping(value = "/class/del", method = RequestMethod.POST)
	public @ResponseBody
	Clazz delClass(@RequestBody Clazz clazz, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		return clazz;
	}
	
	/**
	 * @wr.brief list课程 
	 * @wr.tag Class 
	 * @author Adams 
	 * @version 0.0.1 
	 * @wr.memo init add api 
	 * @author Bob 
	 * @version 0.0.2 
	 * @wr.memo fix bug
	 * @wr.returnCode 400 404 503
	 * @return the class list.
	 */
	@RequestMapping(value = "/class/list", method = RequestMethod.GET)
	public @ResponseBody
	List<Clazz> listClass(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		LinkedList<Clazz> result = new LinkedList<Clazz>();
		result.add(new Clazz());
		return result;
	}
	
}
