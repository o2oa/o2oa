package com.x.organization.assemble.personal.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/definition/*" }, asyncSupported = true)
public class DefinitionJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
