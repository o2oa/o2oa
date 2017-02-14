package com.x.base.core.application.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/echo/*" })
public class EchoJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
