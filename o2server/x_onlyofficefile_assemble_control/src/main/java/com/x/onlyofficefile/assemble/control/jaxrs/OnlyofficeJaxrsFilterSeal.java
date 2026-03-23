package com.x.onlyofficefile.assemble.control.jaxrs;


import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/onlyofficeseal/*", asyncSupported = true)
public class OnlyofficeJaxrsFilterSeal extends ManagerUserJaxrsFilter {
	
 }
