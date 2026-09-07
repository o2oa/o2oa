package com.x.program.center.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;
import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/output/*", asyncSupported = true)
public class OutputJaxrsFilter extends CipherManagerJaxrsFilter {

}
