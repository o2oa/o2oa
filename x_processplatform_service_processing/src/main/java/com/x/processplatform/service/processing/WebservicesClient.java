package com.x.processplatform.service.processing;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

public class WebservicesClient {
	public Object[] jaxws(String wsdl, String method, Object... objects) {
		Object[] result = null;
		try {
			JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
			Client client = dcf.createClient(wsdl);
			result = client.invoke(method, objects);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String jaxrs(String url, String method, String body) {
		return null;
	}
}
