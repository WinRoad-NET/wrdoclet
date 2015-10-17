package net.winroad.Controller;

import javax.servlet.http.HttpServletRequest;

import net.winroad.Models.Gender;
import net.winroad.Models.Student;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/student")
public class StudentController {
	/**
	 * @param name the name of student.
	 * @return the student object.
	 */
	@RequestMapping(value = "name.{json|html}", method = RequestMethod.GET)
	public @ResponseBody
	Student getStudentInJSON(@PathVariable("json|html") String name) {
		Student s = new Student();
		//s.setSno(sno);
		s.setName(name);
		s.setAge(11);
		return s;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody
	Object addStudent(@RequestBody Student student) {
		student.setAge(student.getAge() + 10);
		student.setName(student.saySomething() + student.getTEast());
		return student;
	}

	@RequestMapping(value = "/delete/{name}", method = RequestMethod.POST)
	public @ResponseBody
	String delStudent(@RequestBody(required=false) Student student,
			@PathVariable("name") String username) {
		return "delete name:" + username + "/" + student.getName();
	}

	// TODO: how to handle Object as response?
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public @ResponseBody
	Object updateStudent(@RequestHeader("Accept-Encoding") Student student, 
			@RequestParam(value = "action", required = false) String action) {
		student.setAge(student.getAge() + 100);
		return student;
	}

	@RequestMapping(value = "/getgender/{name}", method = RequestMethod.GET)
	public @ResponseBody
	Gender getStudentGender(@PathVariable("name") String username) {
		return Gender.MALE;
	}
	
	/**
	 * @wr.param username || String || user's name to login || Y || adams | init add | 0.1 | bob | update | 0.2
	 * @wr.param password || String || user's password to login 
	 * @wr.param isAdmin || boolean || login as admin || N || adams | init add | 0.1 
	 * @wr.return login cookie || http cookie
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletRequest request) {
		String username = request.getParameter("username").trim();
		return username;
	}
	
}