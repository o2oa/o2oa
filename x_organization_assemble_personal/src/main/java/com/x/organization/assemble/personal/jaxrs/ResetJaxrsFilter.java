package com.x.organization.assemble.personal.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/reset/*" })
public class ResetJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
