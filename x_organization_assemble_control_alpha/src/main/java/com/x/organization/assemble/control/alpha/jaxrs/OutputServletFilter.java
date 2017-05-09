package com.x.organization.assemble.control.alpha.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { "/servlet/output" })
public class OutputServletFilter extends CipherManagerJaxrsFilter {

}
