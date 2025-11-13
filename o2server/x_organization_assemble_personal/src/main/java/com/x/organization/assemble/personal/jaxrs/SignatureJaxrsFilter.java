package com.x.organization.assemble.personal.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;
import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;
import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/jaxrs/signature/*" }, asyncSupported = true)
public class SignatureJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
