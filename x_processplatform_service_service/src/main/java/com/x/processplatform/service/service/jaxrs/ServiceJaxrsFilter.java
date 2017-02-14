package com.x.processplatform.service.service.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/service/*" })
public class ServiceJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
