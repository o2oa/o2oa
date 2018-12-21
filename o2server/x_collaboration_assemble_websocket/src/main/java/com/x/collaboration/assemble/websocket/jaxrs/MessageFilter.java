package com.x.collaboration.assemble.websocket.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/message/*" })
public class MessageFilter extends CipherManagerJaxrsFilter {

}
