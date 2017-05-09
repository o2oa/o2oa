package com.x.portal.assemble.designer.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/id/*" })
public class IdJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
