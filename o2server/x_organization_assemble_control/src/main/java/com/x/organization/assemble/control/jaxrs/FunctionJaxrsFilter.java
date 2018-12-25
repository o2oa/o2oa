package com.x.organization.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/function/*", asyncSupported = true)
public class FunctionJaxrsFilter extends CipherManagerJaxrsFilter {

}
