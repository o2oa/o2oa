package o2.collect.assemble.jaxrs;

import javax.servlet.annotation.WebFilter;

import o2.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/unit/*", asyncSupported = true)
public class UnitJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
