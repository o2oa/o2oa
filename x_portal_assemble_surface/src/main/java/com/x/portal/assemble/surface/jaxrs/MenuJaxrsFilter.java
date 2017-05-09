package com.x.portal.assemble.surface.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/menu/*")
public class MenuJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
