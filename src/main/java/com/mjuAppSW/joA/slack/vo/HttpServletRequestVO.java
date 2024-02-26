package com.mjuAppSW.joA.slack.vo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public class HttpServletRequestVO {
	private HttpServletRequest httpServletRequest;

	public HttpServletRequestVO(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}
}
