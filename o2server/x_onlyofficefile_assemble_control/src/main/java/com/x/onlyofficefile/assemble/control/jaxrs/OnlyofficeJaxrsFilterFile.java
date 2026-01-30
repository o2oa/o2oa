package com.x.onlyofficefile.assemble.control.jaxrs;


import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/onlyofficefile/*", asyncSupported = true)
public class OnlyofficeJaxrsFilterFile extends AnonymousCipherManagerUserJaxrsFilter {
	
 }
