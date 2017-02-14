package com.x.organization.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/function/*" })
public class FunctionJaxrsFilter extends CipherManagerJaxrsFilter {

}
