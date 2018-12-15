package com.x.base.core.project.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/druid/*" })
public class DruidFilter extends CipherManagerJaxrsFilter {

}