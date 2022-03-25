package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/datastructure/*", asyncSupported = true)
public class DataStructureJaxrsFilter extends ManagerUserJaxrsFilter {

}
