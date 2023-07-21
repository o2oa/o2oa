package com.x.processplatform.assemble.surface.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/sign/*", asyncSupported = true)
public class SignJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
