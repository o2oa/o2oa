package com.x.processplatform.service.processing.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/job/*" })
public class JobJaxrsFilter extends CipherManagerJaxrsFilter {

}
