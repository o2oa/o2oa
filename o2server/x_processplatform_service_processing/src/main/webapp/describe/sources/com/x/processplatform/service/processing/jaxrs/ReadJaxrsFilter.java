package com.x.processplatform.service.processing.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/read/*", asyncSupported = true)
public class ReadJaxrsFilter extends CipherManagerJaxrsFilter {

}
