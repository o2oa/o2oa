package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/agent/*", asyncSupported = true)
public class AgentJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
