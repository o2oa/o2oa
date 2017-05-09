package com.x.organization.assemble.control.alpha.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/inputperson/*" }, asyncSupported = true)
public class InputPersonFilter extends CipherManagerJaxrsFilter {

}
