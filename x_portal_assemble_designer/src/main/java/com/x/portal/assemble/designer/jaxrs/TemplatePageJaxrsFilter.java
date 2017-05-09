package com.x.portal.assemble.designer.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/templatepage/*" })
public class TemplatePageJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
