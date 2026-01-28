package com.x.message.assemble.communicate.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;
import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/consume/*", asyncSupported = true)
public class ConsumeJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
