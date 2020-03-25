package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/adminlogin/*", asyncSupported = true)
public class AdminLoginJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
