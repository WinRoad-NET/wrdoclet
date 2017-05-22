package net.winroad.dubbo;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.alibaba.dubbo.config.annotation.Service; 

@Service(version = "1.0.0")
public class AnnotationServiceImpl implements AnnotationService {

	@Override
	public boolean login(@NotNull @NotEmpty String username,@NotNull @NotEmpty String password) {
		if (username != null && password != null && username.equals(password)) {
			return true;
		}
		return false;
	}
}
