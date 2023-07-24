package com.x.processplatform.service.processing.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/attachment/*", asyncSupported = true)
public class AttachmentJaxrsFilter extends CipherManagerJaxrsFilter {

}
