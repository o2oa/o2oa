package com.x.organization.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/unitattribute/*", asyncSupported = true)
public class UnitAttributeFilter extends CipherManagerUserJaxrsFilter {

}
