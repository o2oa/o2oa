package com.x.pan.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

/**
 * web服务过滤器，无需登录及可访问
 * @author sword
 */
@WebFilter(urlPatterns = "/jaxrs/wopi/*", asyncSupported = true)
public class AnonymousJaxrsServicePathFilter extends AnonymousCipherManagerUserJaxrsFilter {
}
