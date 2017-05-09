package com.x.base.core.application.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/jaxrs/cache/*" })
public class CacheJaxrsFilter extends CipherManagerJaxrsFilter {

}
