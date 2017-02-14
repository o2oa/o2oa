package com.x.test.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class JaxwsTest {
	@WebMethod
	public String echo(String str) {
		return "hello." + str;
	}
}
