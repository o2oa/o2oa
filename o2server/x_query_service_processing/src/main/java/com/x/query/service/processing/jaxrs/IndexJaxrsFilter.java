package com.x.query.service.processing.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/index/*", asyncSupported = true)
public class IndexJaxrsFilter extends CipherManagerJaxrsFilter {

}
