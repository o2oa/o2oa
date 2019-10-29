package com.x.processplatform.service.processing.processor.invoke;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.webservices.WebservicesClient;

public class InvokeExecutor {

	private static Logger logger = LoggerFactory.getLogger(InvokeExecutor.class);

	public String execute(JaxrsObject o) throws Exception {
		String result = "";
		switch (StringUtils.lowerCase(o.getMethod())) {
		case "post":
			result = this.jaxrsHttpPost(o);
			break;
		case "put":
			result = this.jaxrsHttpPut(o);
			break;
		case "get":
			result = this.jaxrsHttpGet(o);
			break;
		case "delete":
			result = this.jaxrsHttpDelete(o);
			break;
		case "head":
			break;
		case "options":
			break;
		case "patch":
			break;
		case "trace":
			break;
		default:
			break;
		}
		return result;
	}

	public Object execute(JaxwsObject o) throws Exception {
		WebservicesClient client = new WebservicesClient();
		return client.jaxws(o.getAddress(), o.getMethod(), o.getParameters());
	}

	private String jaxrsHttpPost(JaxrsObject jaxrsObject) throws Exception {
		if (jaxrsObject.getInternal()) {
			return CipherConnectionAction.post(true, jaxrsObject.getAddress(), jaxrsObject.getBody()).getData()
					.toString();
		} else {
			List<NameValuePair> heads = new ArrayList<>();
			heads.add(new NameValuePair(HttpConnection.Content_Type, jaxrsObject.getContentType()));
			if (null != jaxrsObject.getHead()) {
				for (Entry<String, String> entry : jaxrsObject.getHead().entrySet()) {
					heads.add(new NameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			return HttpConnection.postAsString(jaxrsObject.getAddress(), heads, jaxrsObject.getBody());
		}
	}

	private String jaxrsHttpPut(JaxrsObject jaxrsObject) throws Exception {
		if (jaxrsObject.getInternal()) {
			return CipherConnectionAction.post(true, jaxrsObject.getAddress(), jaxrsObject.getBody()).getData()
					.toString();
		} else {
			List<NameValuePair> heads = new ArrayList<>();
			heads.add(new NameValuePair(HttpConnection.Content_Type, jaxrsObject.getContentType()));
			if (null != jaxrsObject.getHead()) {
				for (Entry<String, String> entry : jaxrsObject.getHead().entrySet()) {
					heads.add(new NameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			return HttpConnection.postAsString(jaxrsObject.getAddress(), heads, jaxrsObject.getBody());
		}
	}

	private String jaxrsHttpGet(JaxrsObject jaxrsObject) throws Exception {
		if (jaxrsObject.getInternal()) {
			return CipherConnectionAction.get(true, jaxrsObject.getAddress()).getData().toString();
		} else {
			List<NameValuePair> heads = new ArrayList<>();
			heads.add(new NameValuePair(HttpConnection.Content_Type, jaxrsObject.getContentType()));
			if (null != jaxrsObject.getHead()) {
				for (Entry<String, String> entry : jaxrsObject.getHead().entrySet()) {
					heads.add(new NameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			return HttpConnection.getAsString(jaxrsObject.getAddress(), heads);
		}
	}

	private String jaxrsHttpDelete(JaxrsObject jaxrsObject) throws Exception {
		if (jaxrsObject.getInternal()) {
			return CipherConnectionAction.delete(true, jaxrsObject.getAddress()).getData().toString();
		} else {
			List<NameValuePair> heads = new ArrayList<>();
			heads.add(new NameValuePair(HttpConnection.Content_Type, jaxrsObject.getContentType()));
			if (null != jaxrsObject.getHead()) {
				for (Entry<String, String> entry : jaxrsObject.getHead().entrySet()) {
					heads.add(new NameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			return HttpConnection.deleteAsString(jaxrsObject.getAddress(), heads);
		}
	}

}
