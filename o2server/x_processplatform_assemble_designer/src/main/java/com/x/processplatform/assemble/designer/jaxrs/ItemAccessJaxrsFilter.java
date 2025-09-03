package com.x.processplatform.assemble.designer.jaxrs;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;
import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/item-access/*", asyncSupported = true)
public class ItemAccessJaxrsFilter extends ManagerUserJaxrsFilter {

}
