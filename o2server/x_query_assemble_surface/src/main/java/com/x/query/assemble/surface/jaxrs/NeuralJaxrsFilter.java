package com.x.query.assemble.surface.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;
import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/neural/*", asyncSupported = true)
public class NeuralJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
