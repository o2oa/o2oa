package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/market/*", asyncSupported = true)
public class MarketJaxrsFilter extends CipherManagerJaxrsFilter {

}
