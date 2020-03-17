package com.x.file.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/attachment2/*", asyncSupported = true)
public class Attachment2JaxrsFilter extends CipherManagerUserJaxrsFilter {

}
