package com.x.file.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/folder2/*",asyncSupported = true)
public class Folder2JaxrsFilter extends CipherManagerUserJaxrsFilter {

}
