package com.x.teamwork.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { 		
		"/jaxrs/project/*",
		"/jaxrs/chat/*",
		"/jaxrs/task/*",
		"/jaxrs/task_tag/*",
		"/jaxrs/attachment/*",
		"/jaxrs/project_group/*",
		"/jaxrs/task_group/*",
		"/jaxrs/task_list/*",
		"/jaxrs/config/*",
		"/jaxrs/extfield/*",
		"/jaxrs/dynamic/*"
		}, asyncSupported = true)
public class JaxrsManagerUserFilter extends ManagerUserJaxrsFilter {
}