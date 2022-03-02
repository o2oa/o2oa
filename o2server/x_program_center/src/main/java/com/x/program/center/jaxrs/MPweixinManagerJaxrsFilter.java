package com.x.program.center.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = {
        "/jaxrs/mpweixin/menu/*",
        "/jaxrs/mpweixin/media/*"
}, asyncSupported = true)
public class MPweixinManagerJaxrsFilter extends CipherManagerJaxrsFilter {

	
	
	
}
