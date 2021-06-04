package com.x.query.assemble.designer.jaxrs;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;
import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/importmodel/*", asyncSupported = true)
public class ImportModelJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
