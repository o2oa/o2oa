package com.x.bbs.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

/**
 * web服务过滤器，将指定的URL定义为需要用户认证的服务，如果用户未登录，则无法访问该服务
 */
@WebFilter( urlPatterns = {
		"/jaxrs/user/*",
		"/servlet/*"
} , asyncSupported = true)
public class BBSJaxrsManagerUserFilter extends CipherManagerUserJaxrsFilter {}
