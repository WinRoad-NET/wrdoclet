package net.winroad.Controller;

import net.winroad.Models.Student;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/student")
public class StudentController {
	@RequestMapping(value = "{name}", method = RequestMethod.GET)
	public @ResponseBody
	Student getStudentInJSON(@PathVariable String name) {
		Student s = new Student();
		s.setName(name);
		s.setAge(11);
		s.setWeight(100);
		return s;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody
	Object addStudent(@RequestBody Student person) {
		person.setAge(person.getAge() + 10);
		return person;
	}
}