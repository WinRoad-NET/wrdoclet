package net.winroad.dubbo;

import lombok.Data;

@Data
public class ChangePasswordRequest extends BaseRequest {
	/**
	 * 旧密码
	 */
	private String oldPassword;
	
	/**
	 * 新密码
	 */
	private String newPassword;
}
