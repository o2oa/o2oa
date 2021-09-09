package com.x.general.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/upgrade/*", asyncSupported = true)
public class UpgradeJaxrsFilter extends CipherManagerJaxrsFilter {

}
