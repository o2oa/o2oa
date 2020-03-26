package com.x.processplatform.assemble.surface.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/serialnumber/*", asyncSupported = true)
public class SerialNumberJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
