package com.x.program.center.jaxrs;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/andfx/*", asyncSupported = true)
public class AndFxJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
