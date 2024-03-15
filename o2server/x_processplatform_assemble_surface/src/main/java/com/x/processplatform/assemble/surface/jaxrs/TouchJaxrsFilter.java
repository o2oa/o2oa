package com.x.processplatform.assemble.surface.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/touch/*", asyncSupported = true)
public class TouchJaxrsFilter extends CipherManagerJaxrsFilter {

}
