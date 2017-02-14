package com.x.processplatform.assemble.bam.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/state/*" })
public class StateJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
