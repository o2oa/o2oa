package com.x.organization.assemble.authentication.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = {"/jaxrs/mpweixin/bind/*", "/jaxrs/mpweixin/menu/*"}, asyncSupported = true)
public class MPweixinJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
