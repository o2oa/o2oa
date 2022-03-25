package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/input/*", asyncSupported = true)
public class InputJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
