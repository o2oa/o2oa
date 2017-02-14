package com.x.okr.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.ManagerUserJaxrsFilter;

/**
 * web服务过滤器，将指定的URL定义为需要用户认证的服务，如果用户未登录，则无法访问该服务
 * @author liyi *
 */
@WebFilter(urlPatterns = {
		"/jaxrs/uuid/*",
		"/jaxrs/login/*",
		"/jaxrs/logout/*",
		"/jaxrs/okrpermissioninfo/*",
		"/jaxrs/okrpersonpermission/*",
		"/jaxrs/okrroleinfo/*",
		"/jaxrs/okrrolepermission/*",
		"/jaxrs/okrauthorize/*",
		"/jaxrs/okrworkchat/*",
		"/jaxrs/okrattachmentfileinfo/*",
		"/jaxrs/okrcenterworkinfo/*",
		"/jaxrs/okrconfigsystem/*",
		"/jaxrs/okrconfigworklevel/*",
		"/jaxrs/okrconfigworktype/*",
		"/jaxrs/okrconfigsecretary/*",
		"/jaxrs/okrtask/*",
		"/jaxrs/okrtaskhandled/*",
		"/jaxrs/okrworkauthorizerecord/*",
		"/jaxrs/okrworkbaseinfo/*",
		"/jaxrs/okrworkdetailinfo/*",
		"/jaxrs/okrworkdynamics/*",
		"/jaxrs/okrworkprobleminfo/*",
		"/jaxrs/okrworkproblempersonlink/*",
		"/jaxrs/okrworkproblemprocesslog/*",
		"/jaxrs/okrworkprocesslink/*",
		"/jaxrs/okrworkreportbaseinfo/*",
		"/jaxrs/okrworkreportdetailinfo/*",
		"/jaxrs/okrworkreportpersonlink/*",
		"/jaxrs/okrworkreportprocesslog/*",
		"/jaxrs/workreportstatistic/*",
		"/jaxrs/admin/okrcenterworkinfo/*",
		"/jaxrs/admin/okrworkbaseinfo/*",
		"/jaxrs/admin/okrtask/*",
		"/jaxrs/admin/okrtaskhandled/*",
		"/jaxrs/admin/okrworkreportbaseinfo/*",
		"/servlet/*"
})
public class OkrJaxrsFilter extends ManagerUserJaxrsFilter {
	
}