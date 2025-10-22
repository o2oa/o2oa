package com.x.program.center.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;
import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/config-open/*", asyncSupported = true)
public class ConfigOpenJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
