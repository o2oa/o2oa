package com.x.base.core.project.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/describe/*" }, asyncSupported = true)
public class DescribeFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
