package com.x.organization.assemble.authentication.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/bind/*" })
public class BindJaxrsFilter extends CipherManagerJaxrsFilter {

}
