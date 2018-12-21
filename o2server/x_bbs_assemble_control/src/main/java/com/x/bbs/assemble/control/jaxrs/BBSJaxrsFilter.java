package com.x.bbs.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

/**
 * web服务过滤器，匿名用户可以访问的服务
 * @author liyi *
 */
@WebFilter( urlPatterns = {
		"/jaxrs/permission/*",
		"/jaxrs/subjectattach/*",
		"/jaxrs/forum/*",
		"/jaxrs/section/*",
		"/jaxrs/subject/*",
		"/jaxrs/reply/*",
		"/jaxrs/userinfo/*",
		"/jaxrs/login/*",
		"/jaxrs/logout/*",
		"/jaxrs/mobile/*",
		"/jaxrs/picture/*",
		"/jaxrs/attachment/*"
} , asyncSupported = true)
public class BBSJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {}