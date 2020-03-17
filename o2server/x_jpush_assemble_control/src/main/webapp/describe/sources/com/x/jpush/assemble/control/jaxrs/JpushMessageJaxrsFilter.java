package com.x.jpush.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/message/*", asyncSupported = true)
public class JpushMessageJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
