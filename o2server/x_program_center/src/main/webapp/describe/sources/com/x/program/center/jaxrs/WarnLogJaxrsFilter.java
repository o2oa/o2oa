package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/warnlog/*", asyncSupported = true)
public class WarnLogJaxrsFilter extends CipherManagerJaxrsFilter {

}
