package com.x.query.assemble.surface.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/query/*", asyncSupported = true)
public class QueryJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
