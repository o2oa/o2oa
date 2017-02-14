package com.x.cms.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.ManagerUserJaxrsFilter;

/**
 * web服务过滤器，将指定的URL定义为需要用户认证的服务，如果用户未登录，则无法访问该服务
 * 
 * @author liyi *
 */
@WebFilter(urlPatterns = { 
		"/jaxrs/appcatagoryadmin/*", 
		"/jaxrs/appcatagorypermission/*", 
		"/jaxrs/appinfo/*",
		"/jaxrs/catagoryinfo/*", 
		"/jaxrs/data/*", 
		"/jaxrs/document/*", 
		"/jaxrs/fileinfo/*", 
		"/jaxrs/form/*",
		"/jaxrs/view/*", 
		"/jaxrs/queryview/*", 
		"/jaxrs/queryview/design/*", 
		"/jaxrs/viewcatagory/*", 
		"/jaxrs/viewfieldconfig/*", 
		"/jaxrs/image/*", 
		"/jaxrs/log/*",
		"/jaxrs/image/*", 
		"/jaxrs/appdict/*", 
		"/jaxrs/appdictitem/*", 
		"/jaxrs/script/*", 
		"/jaxrs/uuid/*",
		"/jaxrs/searchfilter/*", 
		"/servlet/*" 
		})
public class CmsJaxrsFilter extends ManagerUserJaxrsFilter {

}