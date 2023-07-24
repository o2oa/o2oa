package com.x.file.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/editor/*", asyncSupported = true)
public class EditorJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
