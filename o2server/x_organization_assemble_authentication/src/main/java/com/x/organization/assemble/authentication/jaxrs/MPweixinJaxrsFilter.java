package com.x.organization.assemble.authentication.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = {"/jaxrs/mpweixin/bind/*", "/jaxrs/mpweixin/menu/*"}, asyncSupported = true)
public class MPweixinJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
