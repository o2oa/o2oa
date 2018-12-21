package com.x.organization.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/test/*", asyncSupported = true)
public class TestFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
