package com.x.general.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;
import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/generalfile/*", asyncSupported = true)
public class FileJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
