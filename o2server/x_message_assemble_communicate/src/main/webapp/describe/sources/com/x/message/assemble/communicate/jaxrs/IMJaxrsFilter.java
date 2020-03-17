package com.x.message.assemble.communicate.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/im/*", asyncSupported = true)
public class IMJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
