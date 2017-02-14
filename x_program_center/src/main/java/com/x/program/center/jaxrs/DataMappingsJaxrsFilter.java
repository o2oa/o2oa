package com.x.program.center.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/datamappings/*" })
public class DataMappingsJaxrsFilter extends CipherManagerJaxrsFilter {

}
