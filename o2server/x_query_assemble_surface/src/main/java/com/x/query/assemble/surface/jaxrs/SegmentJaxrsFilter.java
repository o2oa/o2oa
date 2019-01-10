package com.x.query.assemble.surface.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/segment/*", asyncSupported = true)
public class SegmentJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
