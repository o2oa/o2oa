package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/pms/*", asyncSupported = true)
public class PmsJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
