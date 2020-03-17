package com.x.teamwork.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { 
		"/jaxrs/cipher/*",
		"/jaxrs/manager/*" 
		}, asyncSupported = true)
public class JaxrsCipherManagerFilter extends CipherManagerJaxrsFilter {
}