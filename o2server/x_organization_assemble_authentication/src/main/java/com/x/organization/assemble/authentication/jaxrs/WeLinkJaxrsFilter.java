package com.x.organization.assemble.authentication.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/welink/*", asyncSupported = true)
public class WeLinkJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
