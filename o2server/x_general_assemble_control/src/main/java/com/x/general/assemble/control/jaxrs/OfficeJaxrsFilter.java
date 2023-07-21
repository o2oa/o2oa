package com.x.general.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/office/*", asyncSupported = true)
public class OfficeJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
