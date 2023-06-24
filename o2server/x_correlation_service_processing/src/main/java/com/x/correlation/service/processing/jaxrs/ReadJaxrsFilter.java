package com.x.correlation.service.processing.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/read/*", asyncSupported = true)
public class ReadJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
