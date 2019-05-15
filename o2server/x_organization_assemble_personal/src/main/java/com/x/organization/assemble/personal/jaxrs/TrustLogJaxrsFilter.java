package com.x.organization.assemble.personal.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/trustlog/*", asyncSupported = true)
public class TrustLogJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
