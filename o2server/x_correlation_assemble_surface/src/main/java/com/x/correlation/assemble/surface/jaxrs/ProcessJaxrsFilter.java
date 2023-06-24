package com.x.correlation.assemble.surface.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/process/*", asyncSupported = true)
public class ProcessJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
