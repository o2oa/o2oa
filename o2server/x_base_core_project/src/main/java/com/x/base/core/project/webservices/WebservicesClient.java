package com.x.base.core.project.webservices;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

public class WebservicesClient {
	public Object[] jaxws(String wsdl, String method, Object... objects) throws Exception {
		Object[] result = null;
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		try (Client client = dcf.createClient(wsdl)) {
			result = client.invoke(method, objects);
		}
		return result;
	}
}
