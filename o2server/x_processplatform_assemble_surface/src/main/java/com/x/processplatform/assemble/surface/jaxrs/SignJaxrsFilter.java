package com.x.processplatform.assemble.surface.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/sign/*", asyncSupported = true)
public class SignJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
