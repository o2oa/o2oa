package com.x.base.core.project.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/jaxrs/echo/*" }, asyncSupported = true)
public class EchoJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
