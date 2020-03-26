package com.x.okr.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/task/*" }, asyncSupported = true)
public class OkrAnonyJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
