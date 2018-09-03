package com.x.message.assemble.communicate.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/consume/*", asyncSupported = true)
public class ConsumeJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
