package com.x.test.jaxrs.test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("test")
public class TestAction {

	@GET
	public Response test1(@Context HttpServletRequest request) {
		Logger logger = LoggerFactory.getLogger(TestAction.class);
		logger.info("Hello World");
		return Response.ok("sssssssss").build();
	}
}