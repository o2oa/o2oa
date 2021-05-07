package com.x.program.center.jaxrs;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/mpweixin/check", asyncSupported = true)
public class MPweixinJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
