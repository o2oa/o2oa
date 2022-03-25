package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/mpweixin/check", asyncSupported = true)
public class MPweixinJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
