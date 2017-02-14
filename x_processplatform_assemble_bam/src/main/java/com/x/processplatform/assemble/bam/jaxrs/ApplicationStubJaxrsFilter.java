package com.x.processplatform.assemble.bam.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/applicationstub/*" })
public class ApplicationStubJaxrsFilter extends CipherManagerJaxrsFilter {

}
