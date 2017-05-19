package net.winroad.dubbo;
import com.alibaba.dubbo.config.annotation.Service; 

@Service(version = "1.0.0")
public class AnnotationServiceImpl implements AnnotationService {

	@Override
	public boolean login(String username, String password) {
		if (username != null && password != null && username.equals(password)) {
			return true;
		}
		return false;
	}
}
