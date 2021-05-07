package com.x.organization.assemble.authentication.jaxrs;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/mpweixin/login/*", asyncSupported = true)
public class MPweixinAnonymousJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
