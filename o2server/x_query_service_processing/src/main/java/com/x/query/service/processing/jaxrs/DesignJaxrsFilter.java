package com.x.query.service.processing.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/design/*", asyncSupported = true)
public class DesignJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
