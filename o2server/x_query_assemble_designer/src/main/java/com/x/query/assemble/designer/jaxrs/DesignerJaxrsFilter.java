package com.x.query.assemble.designer.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/designer/*", asyncSupported = true)
public class DesignerJaxrsFilter extends CipherManagerJaxrsFilter {

}
