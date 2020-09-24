package com.x.base.core.project.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/jaxrs/thread/*" }, asyncSupported = true)
public class ThreadJaxrsFilter extends CipherManagerJaxrsFilter {

}
