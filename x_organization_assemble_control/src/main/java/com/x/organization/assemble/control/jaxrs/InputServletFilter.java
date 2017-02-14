package com.x.organization.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { "/servlet/input" })
public class InputServletFilter extends CipherManagerJaxrsFilter {

}
