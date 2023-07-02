package com.x.organization.assemble.express.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/person/*", asyncSupported = true)
public class PersonJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
