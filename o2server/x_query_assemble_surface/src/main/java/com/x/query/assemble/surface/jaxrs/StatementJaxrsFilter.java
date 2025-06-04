package com.x.query.assemble.surface.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;
import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/statement/*", asyncSupported = true)
public class StatementJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
