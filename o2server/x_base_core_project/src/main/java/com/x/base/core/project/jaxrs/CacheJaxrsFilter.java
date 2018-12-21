package com.x.base.core.project.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/jaxrs/cache/*" })
public class CacheJaxrsFilter extends CipherManagerJaxrsFilter {

}
