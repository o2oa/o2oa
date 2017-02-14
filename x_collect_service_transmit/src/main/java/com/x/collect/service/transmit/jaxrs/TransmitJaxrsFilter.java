package com.x.collect.service.transmit.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/transmit/*" })
public class TransmitJaxrsFilter extends CipherManagerJaxrsFilter {

}
