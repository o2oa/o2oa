package com.x.report.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

/**
 * web服务过滤器，将指定的URL定义为需要用户认证的服务，如果用户未登录，则无法访问该服务
 * 
 * @author liyi *
 */
@WebFilter(urlPatterns = {
	"/servlet/*",
	"/jaxrs/report/*",
	"/jaxrs/setting/*",
	"/jaxrs/workplan/*",
	"/jaxrs/workplannext/*",
	"/jaxrs/workprog/*",
	"/jaxrs/reportworkinfo/*",
	"/jaxrs/strategywork/*",
	"/jaxrs/strategyworksnap/*",
	"/jaxrs/profiles/*",
	"/jaxrs/export/*",
	"/jaxrs/stat/*",
	"/jaxrs/ext/*"
}, asyncSupported = true )
public class ReportJaxrsFilter extends ManagerUserJaxrsFilter {

}