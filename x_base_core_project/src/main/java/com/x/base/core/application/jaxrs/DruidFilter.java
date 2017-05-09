package com.x.base.core.application.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/druid/*" })
public class DruidFilter extends CipherManagerJaxrsFilter {

}