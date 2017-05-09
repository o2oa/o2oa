package com.x.base.core.application.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/jaxrs/echo/*" })
public class EchoJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
