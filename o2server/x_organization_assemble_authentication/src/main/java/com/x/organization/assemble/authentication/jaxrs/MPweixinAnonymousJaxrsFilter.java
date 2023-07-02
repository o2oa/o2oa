package com.x.organization.assemble.authentication.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/mpweixin/login/*", asyncSupported = true)
public class MPweixinAnonymousJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
