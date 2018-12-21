package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/jest/*", asyncSupported = true)
public class ClockJaxrsFilter extends CipherManagerJaxrsFilter {

}
