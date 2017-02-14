package com.x.processplatform.service.processing;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

public class JaxwsClient {
	public Object[] invoke(String wsdl, String method, Object... objects) {
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
}
