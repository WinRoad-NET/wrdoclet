package net.winroad.Controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController {
	/**
	 * 取得输入参数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return
	 */
	public Map<String, String> getInputParam(HttpServletRequest request) {
		Map<String, String> reqs = new HashMap<String, String>();
		@SuppressWarnings("unchecked")
		Enumeration<String> param = request.getParameterNames();

		while (param.hasMoreElements()) {
			String pname = param.nextElement().toString();
			reqs.put(pname, request.getParameter(pname));
		}
		return reqs;
	}
}
