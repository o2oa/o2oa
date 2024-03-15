package com.x.portal.assemble.designer.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = {"/jaxrs/page/*", "/jaxrs/pageversion/*"}, asyncSupported = true)
public class PageJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
