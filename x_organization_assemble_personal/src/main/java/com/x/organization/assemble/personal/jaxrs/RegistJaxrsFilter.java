package com.x.organization.assemble.personal.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/regist/*" })
public class RegistJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
