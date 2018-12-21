package com.x.strategydeploy.assemble.control.jaxrs;
import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/strategydeployextra/*", "/jaxrs/measuresextra/*", "/jaxrs/keyworkextra/*" }, asyncSupported = true)
public class JaxrsCipherManagerUserJaxrsFilter extends CipherManagerUserJaxrsFilter{

}
