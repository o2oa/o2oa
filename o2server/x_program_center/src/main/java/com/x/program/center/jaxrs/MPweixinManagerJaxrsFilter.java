package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = {
        "/jaxrs/mpweixin/menu/*",
        "/jaxrs/mpweixin/media/*"
}, asyncSupported = true)
public class MPweixinManagerJaxrsFilter extends CipherManagerJaxrsFilter {

	
	
	
}
