package com.x.pan.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/attachment3/*", asyncSupported = true)
public class Attachment3JaxrsFilter extends CipherManagerUserJaxrsFilter {

}
