package com.x.portal.assemble.surface.jaxrs;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/dict/*", asyncSupported = true)
public class DictJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
