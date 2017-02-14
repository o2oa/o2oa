package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/applications/*" })
public class ApplicationsJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
