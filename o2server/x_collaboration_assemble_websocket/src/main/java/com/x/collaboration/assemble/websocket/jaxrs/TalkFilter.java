package com.x.collaboration.assemble.websocket.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/talk/*" })
public class TalkFilter extends CipherManagerUserJaxrsFilter {

}
