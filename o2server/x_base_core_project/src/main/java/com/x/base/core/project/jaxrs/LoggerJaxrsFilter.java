package com.x.base.core.project.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/jaxrs/logger/*" })
public class LoggerJaxrsFilter extends CipherManagerJaxrsFilter {

}
