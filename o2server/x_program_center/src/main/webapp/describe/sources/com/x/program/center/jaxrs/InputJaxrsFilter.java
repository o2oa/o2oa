package com.x.program.center.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/input/*", asyncSupported = true)
public class InputJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
