package net.winroad.dubbo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.NonNull;
import lombok.Setter;

public class BaseRequest {
	/**
	 * the id of the client
	 */
	@Setter @NonNull
	@SuppressFBWarnings(value="URF_UNREAD_FIELD", justification = "just for demo")
	private String clientId;
	
	
	@SuppressFBWarnings(value="UUF_UNUSED_FIELD", justification = "just for demo")
	private String deviceType;
}
