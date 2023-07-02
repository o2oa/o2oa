package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/invoke/*", asyncSupported = true)
public class InvokeJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
