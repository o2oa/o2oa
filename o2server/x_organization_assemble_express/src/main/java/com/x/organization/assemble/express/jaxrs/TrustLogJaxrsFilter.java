package com.x.organization.assemble.express.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/trustlog/*", asyncSupported = true)
public class TrustLogJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
