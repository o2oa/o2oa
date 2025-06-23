package com.x.processplatform.assemble.designer.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/mergeitemplan/*", asyncSupported = true)
public class MergeItemPlanJaxrsFilter extends CipherManagerJaxrsFilter {

}
