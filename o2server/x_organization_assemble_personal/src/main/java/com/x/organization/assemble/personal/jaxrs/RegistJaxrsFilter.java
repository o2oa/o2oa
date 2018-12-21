package com.x.organization.assemble.personal.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/regist/*", asyncSupported = true)
public class RegistJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
