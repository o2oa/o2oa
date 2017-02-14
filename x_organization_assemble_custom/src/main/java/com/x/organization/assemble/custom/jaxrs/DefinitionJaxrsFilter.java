package com.x.organization.assemble.custom.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/definition/*" })
public class DefinitionJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
