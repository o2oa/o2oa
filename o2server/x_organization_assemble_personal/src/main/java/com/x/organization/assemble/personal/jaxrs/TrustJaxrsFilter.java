package com.x.organization.assemble.personal.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/trust/*", asyncSupported = true)
public class TrustJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
