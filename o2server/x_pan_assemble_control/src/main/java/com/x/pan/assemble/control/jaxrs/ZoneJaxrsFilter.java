package com.x.pan.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/zone/*",asyncSupported = true)
public class ZoneJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
