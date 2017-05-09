package com.x.organization.assemble.control.alpha.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/departmentduty/*" })
public class DepartmentDutyJaxrsFilter extends ManagerUserJaxrsFilter {

}
