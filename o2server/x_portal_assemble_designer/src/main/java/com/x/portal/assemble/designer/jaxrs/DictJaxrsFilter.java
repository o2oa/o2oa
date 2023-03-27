package com.x.portal.assemble.designer.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/dict/*", asyncSupported = true)
public class DictJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
