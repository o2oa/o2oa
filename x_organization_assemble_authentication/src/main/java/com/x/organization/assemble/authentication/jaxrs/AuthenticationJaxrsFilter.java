package com.x.organization.assemble.authentication.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/authentication/*" })
public class AuthenticationJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
