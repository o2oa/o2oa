package com.x.organization.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = {  "/jaxrs/complex/*" })
public class ComplexJaxrsFilter extends ManagerUserJaxrsFilter {

}
