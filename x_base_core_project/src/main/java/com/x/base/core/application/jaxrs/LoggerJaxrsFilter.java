package com.x.base.core.application.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/jaxrs/logger/*" })
public class LoggerJaxrsFilter extends CipherManagerJaxrsFilter {

}
