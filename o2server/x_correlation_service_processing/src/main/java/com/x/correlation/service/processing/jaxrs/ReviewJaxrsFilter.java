package com.x.correlation.service.processing.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/review/*", asyncSupported = true)
public class ReviewJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
