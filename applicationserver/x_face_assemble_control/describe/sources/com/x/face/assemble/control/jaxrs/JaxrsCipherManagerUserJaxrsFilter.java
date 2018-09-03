package com.x.face.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/faceset/*", "/jaxrs/face/*", "/jaxrs/search/*", "/jaxrs/compare/*" }, asyncSupported = true)
public class JaxrsCipherManagerUserJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
