package com.x.processplatform.assemble.bam.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/state/*", asyncSupported = true)
public class StateJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
