package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/apppackanony/*", asyncSupported = true)
public class AppPackAnonymousJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}