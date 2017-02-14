package com.x.common.core.application.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.x.base.core.http.AbstractJsonMessageBodyReader;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class WrapInMessageBodyReader<T> extends AbstractJsonMessageBodyReader<T> {

}