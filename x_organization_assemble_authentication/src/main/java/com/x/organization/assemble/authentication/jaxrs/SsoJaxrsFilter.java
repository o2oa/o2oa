package com.x.organization.assemble.authentication.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/sso/*" })
public class SsoJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
