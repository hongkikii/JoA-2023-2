package com.mjuAppSW.joA.slack.vo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public class HttpServletRequestVO {
	private String requestURL;
	private String method;

	public HttpServletRequestVO(HttpServletRequest request){
		this.requestURL = String.valueOf(request.getRequestURL());
		this.method = request.getMethod();
	}
}
