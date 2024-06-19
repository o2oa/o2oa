package com.x.general.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/excel/*", asyncSupported = true)
public class ExcelJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
