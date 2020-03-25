package com.x.program.center.jaxrs;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/datastructure/*", asyncSupported = true)
public class DataStructureJaxrsFilter extends ManagerUserJaxrsFilter {

}
