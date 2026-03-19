package com.x.onlyofficefile.assemble.control.jaxrs;


import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/onlyofficeconfig/*", asyncSupported = true)
public class OnlyofficeJaxrsFilterConfig extends ManagerUserJaxrsFilter {
	
 }
