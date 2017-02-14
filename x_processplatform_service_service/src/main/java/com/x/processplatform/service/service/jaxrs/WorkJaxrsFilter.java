package com.x.processplatform.service.service.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/work/*" })
public class WorkJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
