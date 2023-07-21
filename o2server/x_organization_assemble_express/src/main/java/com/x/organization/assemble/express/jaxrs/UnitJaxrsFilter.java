package com.x.organization.assemble.express.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/unit/*", asyncSupported = true)
public class UnitJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
