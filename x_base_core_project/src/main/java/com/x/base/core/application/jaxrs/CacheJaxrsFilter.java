package com.x.base.core.application.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/cache/*" })
public class CacheJaxrsFilter extends CipherManagerJaxrsFilter {

}
