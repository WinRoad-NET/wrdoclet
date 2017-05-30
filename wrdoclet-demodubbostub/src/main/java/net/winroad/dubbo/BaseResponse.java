package net.winroad.dubbo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseResponse implements Serializable{
    private static final long serialVersionUID = -8011902554010981544L;

    /**
     * response message.
     */
	private String message;
}
