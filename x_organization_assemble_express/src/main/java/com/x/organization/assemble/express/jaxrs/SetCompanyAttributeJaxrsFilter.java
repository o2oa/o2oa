package com.x.organization.assemble.express.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/setcompanyattribute/*")
public class SetCompanyAttributeJaxrsFilter extends ManagerUserJaxrsFilter {

}
