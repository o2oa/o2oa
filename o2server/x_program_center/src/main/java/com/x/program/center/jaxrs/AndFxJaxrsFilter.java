package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/andfx/*", asyncSupported = true)
public class AndFxJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
