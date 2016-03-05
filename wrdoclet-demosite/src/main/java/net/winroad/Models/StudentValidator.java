package net.winroad.Models;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class StudentValidator implements Validator {

	public boolean supports(Class<?> clazz) {
		return clazz.equals(Student.class);
	}

	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "schoolName", "user.schoolName.required", "schoolName不能为空"); 
	}

}
