package com.x.file.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/recycle/*", asyncSupported = true)
public class RecycleJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
