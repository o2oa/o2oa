package com.x.query.service.processing.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/table/*", asyncSupported = true)
public class TableJaxrsFilter extends CipherManagerJaxrsFilter {

}
