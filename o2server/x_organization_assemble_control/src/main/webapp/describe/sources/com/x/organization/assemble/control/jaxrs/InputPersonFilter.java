package com.x.organization.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/inputperson/*", asyncSupported = true)
public class InputPersonFilter extends CipherManagerJaxrsFilter {

}
