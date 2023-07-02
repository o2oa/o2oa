package com.x.organization.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/export/*", asyncSupported = true)
public class ExportJaxrsFilter extends CipherManagerJaxrsFilter {

}
