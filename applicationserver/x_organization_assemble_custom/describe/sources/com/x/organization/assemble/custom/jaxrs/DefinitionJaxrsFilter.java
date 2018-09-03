package com.x.organization.assemble.custom.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/definition/*" }, asyncSupported = true)
public class DefinitionJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
