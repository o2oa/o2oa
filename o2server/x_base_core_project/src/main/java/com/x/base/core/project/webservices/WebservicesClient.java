package com.x.base.core.project.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.connection.HttpConnection;

public class WebservicesClient {
	public Object[] jaxws(String wsdl, String method, Object... objects) throws Exception {
		Object[] result = null;
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		try (Client client = dcf.createClient(wsdl)) {
			result = client.invoke(method, objects);
		}
		return result;
	}
	
	public  String jaxwsXml(String wsdlURL, String soapXML) throws Exception {
	      List<NameValuePair> heads = new ArrayList<>();
	      heads.add(new NameValuePair(ConnectionAction.CONTENT_TYPE,MediaType.TEXT_XML));
		  return HttpConnection.postAsString( wsdlURL , heads, soapXML);
	}
}
