package com.x.query.assemble.surface.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/neural/*", asyncSupported = true)
public class NeuralJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
