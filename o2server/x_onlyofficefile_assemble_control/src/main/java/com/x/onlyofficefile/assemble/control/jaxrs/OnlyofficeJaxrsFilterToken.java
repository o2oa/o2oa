package com.x.onlyofficefile.assemble.control.jaxrs;


import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/onlyofficetoken/*", asyncSupported = true)
public class OnlyofficeJaxrsFilterToken extends CipherManagerJaxrsFilter {
	
 }
