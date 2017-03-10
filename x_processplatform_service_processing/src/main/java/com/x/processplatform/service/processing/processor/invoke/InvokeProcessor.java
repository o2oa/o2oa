package com.x.processplatform.service.processing.processor.invoke;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.project.server.Config;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.BindingPair;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.WebservicesClient;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;
import com.x.processplatform.service.processing.processor.AbstractProcessor;

public class InvokeProcessor extends AbstractProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(InvokeProcessor.class);

	public InvokeProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Activity activity) throws Exception {
		return work;
	}

	@Override
	protected List<Work> executeProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity) throws Exception {
		List<Work> results = new ArrayList<>();
		Invoke invoke = (Invoke) activity;
		switch (invoke.getInvokeMode()) {
		case jaxws:
			this.jaxws(this.business(), attributes, work, data, invoke);
			break;
		case jaxrs:
			this.jaxrs(this.business(), attributes, work, data, invoke);
			break;
		default:
			break;
		}
		results.add(work);
		return results;
	}

	@Override
	protected List<Route> inquireProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity, List<Route> routes) throws Exception {
		List<Route> results = new ArrayList<>();
		results.add(routes.get(0));
		return results;
	}

	private void jaxws(Business business, ProcessingAttributes attributes, Work work, Data data, Invoke invoke)
			throws Exception {
		Object[] parameters = this.jaxwsEvalParameters(business, attributes, work, data, invoke);
		WebservicesClient client = new WebservicesClient();
		Object response = client.jaxws(invoke.getJaxwsAddress(), invoke.getJaxwsMethod(), parameters);
		if ((StringUtils.isNotEmpty(invoke.getJaxwsResponseScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxwsResponseScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(business, attributes, work, data, invoke,
					new BindingPair("response", response));
			scriptHelper.eval(work.getApplication(), invoke.getJaxwsResponseScript(),
					invoke.getJaxwsResponseScriptText());
		}
	}

	private Object[] jaxwsEvalParameters(Business business, ProcessingAttributes attributes, Work work, Data data,
			Invoke invoke) throws Exception {
		List<?> parameters = new ArrayList<>();
		if ((StringUtils.isNotEmpty(invoke.getJaxwsParameterScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxwsParameterScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(business, attributes, work, data, invoke,
					new BindingPair("parameters", parameters));
			scriptHelper.eval(work.getApplication(), invoke.getJaxwsParameterScript(),
					invoke.getJaxwsParameterScriptText());
		}
		return parameters.toArray();
	}

	private void jaxrs(Business business, ProcessingAttributes attributes, Work work, Data data, Invoke invoke)
			throws Exception {
		String result;
		switch (StringUtils.lowerCase(invoke.getJaxrsMethod())) {
		case "post":
			result = this.httpPost(business, attributes, work, data, invoke);
			break;
		case "put":
			result = this.httpPut(business, attributes, work, data, invoke);
			break;
		case "get":
			result = this.httpGet(business, attributes, work, data, invoke);
			break;
		case "delete":
			result = this.httpDelete(business, attributes, work, data, invoke);
			break;
		case "head":
			result = this.httpHead(business, attributes, work, data, invoke);
			break;
		case "options":
			result = this.httpOptions(business, attributes, work, data, invoke);
			break;
		case "patch":
			result = this.httpPatch(business, attributes, work, data, invoke);
			break;
		case "trace":
			result = this.httpTrace(business, attributes, work, data, invoke);
			break;
		default:
			throw new Exception("unknown http method " + invoke.getJaxrsMethod());
		}
		JaxrsResponse jaxrsResponse = new JaxrsResponse();
		jaxrsResponse.setValue(result);
		if ((StringUtils.isNotEmpty(invoke.getJaxrsResponseScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxrsResponseScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(business, attributes, work, data, invoke,
					new BindingPair("response", jaxrsResponse));
			scriptHelper.eval(work.getApplication(), invoke.getJaxrsResponseScript(),
					invoke.getJaxrsResponseScriptText());
		}
	}

	private String jaxrsUrl(Business business, ProcessingAttributes attributes, Work work, Data data, Invoke invoke)
			throws Exception {
		String url = invoke.getJaxrsAddress();
		Map<String, String> parameters = new HashMap<>();
		if ((StringUtils.isNotEmpty(invoke.getJaxrsParameterScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxrsParameterScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(business, attributes, work, data, invoke,
					new BindingPair("parameters", parameters));
			scriptHelper.eval(work.getApplication(), invoke.getJaxrsParameterScript(),
					invoke.getJaxrsParameterScriptText());
		}
		for (Entry<String, String> entry : parameters.entrySet()) {
			url = StringUtils.replace(url, "{" + entry.getKey() + "}", entry.getValue());
		}
		return url;
	}

	private String jaxrsEvalBody(Business business, ProcessingAttributes attributes, Work work, Data data,
			Invoke invoke) throws Exception {
		JaxrsBody body = new JaxrsBody();
		if ((StringUtils.isNotEmpty(invoke.getJaxrsBodyScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxrsBodyScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(business, attributes, work, data, invoke,
					new BindingPair("body", body));
			scriptHelper.eval(work.getApplication(), invoke.getJaxrsBodyScript(), invoke.getJaxrsBodyScriptText());
		}
		return StringUtils.trimToEmpty(body.get());
	}

	private String httpPost(Business business, ProcessingAttributes attributes, Work work, Data data, Invoke invoke)
			throws Exception {
		String address = this.jaxrsUrl(business, attributes, work, data, invoke);
		HttpURLConnection connection = this.prepareConnection(address, true, invoke.getJaxrsContentType());
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		Object o = this.jaxrsEvalBody(business, attributes, work, data, invoke);
		connection.connect();
		this.doOutput(connection, o);
		return this.readResultString(connection);
	}

	private String httpPut(Business business, ProcessingAttributes attributes, Work work, Data data, Invoke invoke)
			throws Exception {
		String address = this.jaxrsUrl(business, attributes, work, data, invoke);
		HttpURLConnection connection = this.prepareConnection(address, invoke.getJaxrsWithCipher(),
				invoke.getJaxrsContentType());
		connection.setRequestMethod("PUT");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		Object o = this.jaxrsEvalBody(business, attributes, work, data, invoke);
		connection.connect();
		this.doOutput(connection, o);
		return this.readResultString(connection);
	}

	private String httpGet(Business business, ProcessingAttributes attributes, Work work, Data data, Invoke invoke)
			throws Exception {
		String address = this.jaxrsUrl(business, attributes, work, data, invoke);
		HttpURLConnection connection = this.prepareConnection(address, invoke.getJaxrsWithCipher(),
				invoke.getJaxrsContentType());
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		return this.readResultString(connection);

	}

	private String httpDelete(Business business, ProcessingAttributes attributes, Work work, Data data, Invoke invoke)
			throws Exception {
		String address = this.jaxrsUrl(business, attributes, work, data, invoke);
		HttpURLConnection connection = this.prepareConnection(address, invoke.getJaxrsWithCipher(),
				invoke.getJaxrsContentType());
		connection.setRequestMethod("DELETE");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		return this.readResultString(connection);
	}

	private String httpHead(Business business, ProcessingAttributes attributes, Work work, Data data, Invoke invoke)
			throws Exception {
		String address = this.jaxrsUrl(business, attributes, work, data, invoke);
		HttpURLConnection connection = this.prepareConnection(address, invoke.getJaxrsWithCipher(),
				invoke.getJaxrsContentType());
		connection.setRequestMethod("HEAD");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		return this.readResultString(connection);
		// return Request.Head(this.jaxrsUrl(business, attributes, work, data,
		// invoke)).execute().returnResponse();
	}

	private String httpOptions(Business business, ProcessingAttributes attributes, Work work, Data data, Invoke invoke)
			throws Exception {
		// return Request.Options(this.jaxrsUrl(business, attributes, work,
		// data, invoke)).execute().returnResponse();
		String address = this.jaxrsUrl(business, attributes, work, data, invoke);
		HttpURLConnection connection = this.prepareConnection(address, invoke.getJaxrsWithCipher(),
				invoke.getJaxrsContentType());
		connection.setRequestMethod("OPTIONS");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		return this.readResultString(connection);
	}

	private String httpPatch(Business business, ProcessingAttributes attributes, Work work, Data data, Invoke invoke)
			throws Exception {
		String address = this.jaxrsUrl(business, attributes, work, data, invoke);
		HttpURLConnection connection = this.prepareConnection(address, invoke.getJaxrsWithCipher(),
				invoke.getJaxrsContentType());
		connection.setRequestMethod("PATCH");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		return this.readResultString(connection);
	}

	private String httpTrace(Business business, ProcessingAttributes attributes, Work work, Data data, Invoke invoke)
			throws Exception {
		String address = this.jaxrsUrl(business, attributes, work, data, invoke);
		HttpURLConnection connection = this.prepareConnection(address, invoke.getJaxrsWithCipher(),
				invoke.getJaxrsContentType());
		connection.setRequestMethod("TRACE");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		return this.readResultString(connection);
	}

	public class JaxrsBody {

		private String value;

		public String get() {
			return value;
		}

		public void set(Object value) throws Exception {
			if (null != value) {
				this.value = XGsonBuilder.toJson(value);
			}
		}
	}

	private HttpURLConnection prepareConnection(String address, boolean withCipher, String contentType)
			throws Exception {
		try {
			URL url = new URL(address);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setRequestProperty("Content-Type", contentType);
			if (BooleanUtils.isTrue(withCipher)) {
				EffectivePerson effectivePerson = EffectivePerson.cipher(Config.token().getCipher());
				httpUrlConnection.setRequestProperty(HttpToken.X_Token, effectivePerson.getToken());
			}
			return httpUrlConnection;
		} catch (Exception e) {
			throw new Exception("prepareConnection error", e);
		}
	}

	private void doOutput(HttpURLConnection connection, Object data) throws Exception {
		try (OutputStream output = connection.getOutputStream()) {
			if (null != data) {
				if (data instanceof String) {
					IOUtils.write(data.toString(), output, StandardCharsets.UTF_8);
				} else {
					IOUtils.write(XGsonBuilder.toJson(data), output, StandardCharsets.UTF_8);
				}
				output.flush();
			}
		}
	}

	private String readResultString(HttpURLConnection connection) throws Exception {
		String result = "";
		try (InputStream input = connection.getInputStream()) {
			result = IOUtils.toString(input, StandardCharsets.UTF_8);
		}
		return result;
	}

	public class JaxrsResponse {

		private int status;
		private String value;

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

}
