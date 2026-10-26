package com.x.base.core.project.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/jaxrs/describe/*" }, asyncSupported = true)
public class DescribeJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
