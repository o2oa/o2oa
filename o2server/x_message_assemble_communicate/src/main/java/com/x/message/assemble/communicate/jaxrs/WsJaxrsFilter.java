package com.x.message.assemble.communicate.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/ws/*", asyncSupported = true)
public class WsJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
