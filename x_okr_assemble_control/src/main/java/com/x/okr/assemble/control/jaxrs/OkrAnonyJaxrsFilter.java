package com.x.okr.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/task/*" })
public class OkrAnonyJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
