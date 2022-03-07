package com.x.file.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

/**
 * @author sword
 */
@WebFilter(urlPatterns = {
		"/jaxrs/anonymous/*",
		}, asyncSupported = true )
public class FileAnonymousJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
