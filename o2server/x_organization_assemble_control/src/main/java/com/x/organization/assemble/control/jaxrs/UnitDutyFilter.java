package com.x.organization.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/unitduty/*", asyncSupported = true)
public class UnitDutyFilter extends CipherManagerUserJaxrsFilter {

}
