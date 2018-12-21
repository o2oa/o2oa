package com.x.okr.assemble.control.dataadapter.webservice;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import com.google.gson.JsonElement;
import com.x.okr.assemble.control.ThisApplication;

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

	public JsonElement jaxrsGet(String applicationName, String uri) throws Exception {
		Class<?> clz = Class.forName("com.x.base.core.project." + applicationName);
		return ThisApplication.context().applications().getQuery(clz, uri).getData();
	}

	public JsonElement jaxrsPut(String applicationName, String uri, Object o) throws Exception {
		Class<?> clz = Class.forName("com.x.base.core.project." + applicationName);
		return ThisApplication.context().applications().putQuery(clz, uri, o).getData();
	}

	public JsonElement jaxrsPost(String applicationName, String uri, Object o) throws Exception {
		Class<?> clz = Class.forName("com.x.base.core.project." + applicationName);
		return ThisApplication.context().applications().postQuery(clz, uri, o).getData();
	}

	public JsonElement jaxrsDelete(String applicationName, String uri, Object o) throws Exception {
		Class<?> clz = Class.forName("com.x.base.core.project." + applicationName);
		return ThisApplication.context().applications().deleteQuery(clz, uri).getData();
	}

}
