package com.x.pan.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/share/*", asyncSupported = true)
public class ShareJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
