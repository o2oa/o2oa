package com.x.general.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;
import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/office/*", asyncSupported = true)
public class OfficeJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
