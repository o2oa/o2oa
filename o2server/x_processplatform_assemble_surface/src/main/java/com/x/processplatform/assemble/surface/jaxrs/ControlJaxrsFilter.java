package com.x.processplatform.assemble.surface.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/control/*", asyncSupported = true)
public class ControlJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
