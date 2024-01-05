package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/bar/*", asyncSupported = true)
public class BarJaxrsFilter extends CipherManagerJaxrsFilter {

}
