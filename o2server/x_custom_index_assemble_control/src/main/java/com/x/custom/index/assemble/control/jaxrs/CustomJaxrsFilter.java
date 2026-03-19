package com.x.custom.index.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/custom/*", asyncSupported = true)
public class CustomJaxrsFilter extends CipherManagerUserJaxrsFilter {

}