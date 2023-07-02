package com.x.organization.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/permissionsetting/*", asyncSupported = true)
public class PermissionSettingJaxrsFilter extends ManagerUserJaxrsFilter {

}
