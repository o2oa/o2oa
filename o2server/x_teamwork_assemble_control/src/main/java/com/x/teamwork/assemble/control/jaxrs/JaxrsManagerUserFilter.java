package com.x.teamwork.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { 		
		"/jaxrs/project/*",
		"/jaxrs/task/*",
		"/jaxrs/project_group/*",
		"/jaxrs/task_group/*",
		"/jaxrs/task_list/*",
		"/jaxrs/config/*",
		"/jaxrs/extfield/*"
		}, asyncSupported = true)
public class JaxrsManagerUserFilter extends ManagerUserJaxrsFilter {
}