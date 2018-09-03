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
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.exception.RunningException;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.BindingPair;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.WebservicesClient;
import com.x.processplatform.service.processing.WrapScriptObject;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class InvokeProcessor extends AbstractInvokeProcessor {

	private static Logger logger = LoggerFactory.getLogger(InvokeProcessor.class);

	public InvokeProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		return aeiObjects.getWork();
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		List<Work> results = new ArrayList<>();
		switch (invoke.getInvokeMode()) {
		case jaxws:
			this.jaxws(aeiObjects, invoke);
			break;
		case jaxrs:
			this.jaxrs(aeiObjects, invoke);
			break;
		default:
			break;
		}
		results.add(aeiObjects.getWork());
		return results;
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		List<Route> results = new ArrayList<>();
		results.add(aeiObjects.getRoutes().get(0));
		return results;
	}

	private void jaxws(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		if (BooleanUtils.isTrue(invoke.getInternal())) {
			this.jaxwsInternal(aeiObjects, invoke);
		} else {
			this.jaxwsExternal(aeiObjects, invoke);
		}
	}

	private void jaxwsInternal(AeiObjects aeiObjects, Invoke invoke) throws Exception {
	}

	private void jaxwsExternal(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		Object[] parameters = this.jaxwsEvalParameters(aeiObjects, invoke);
		WebservicesClient client = new WebservicesClient();
		Object response = client.jaxws(invoke.getJaxwsAddress(), invoke.getJaxwsMethod(), parameters);
		if ((StringUtils.isNotEmpty(invoke.getJaxwsResponseScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxwsResponseScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects, new BindingPair("response", response));
			scriptHelper.eval(aeiObjects.getWork().getApplication(), invoke.getJaxwsResponseScript(),
					invoke.getJaxwsResponseScriptText());
		}
	}

	private Object[] jaxwsEvalParameters(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		List<?> parameters = new ArrayList<>();
		if ((StringUtils.isNotEmpty(invoke.getJaxwsParameterScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxwsParameterScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects,
					new BindingPair("parameters", parameters));
			scriptHelper.eval(aeiObjects.getWork().getApplication(), invoke.getJaxwsParameterScript(),
					invoke.getJaxwsParameterScriptText());
		}
		return parameters.toArray();
	}

	private void jaxrs(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		if (BooleanUtils.isTrue(invoke.getInternal())) {
			this.jaxrsInternal(aeiObjects, invoke);
		} else {
			this.jaxrsExternal(aeiObjects, invoke);
		}
	}

	private void jaxrsInternal(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		ActionResponse resp = null;
		Class<?> clz = Class.forName("com.x.base.core.project." + invoke.getInternalProject());
		String uri = this.jaxrsUrl(aeiObjects, invoke);
		String body = null;
		switch (StringUtils.lowerCase(invoke.getJaxrsMethod())) {
		case "post":
			body = this.jaxrsEvalBody(aeiObjects, invoke);
			resp = ThisApplication.context().applications().postQuery(clz, uri, body);
			break;
		case "put":
			body = this.jaxrsEvalBody(aeiObjects, invoke);
			resp = ThisApplication.context().applications().putQuery(clz, uri, body);
			break;
		case "get":
			resp = ThisApplication.context().applications().getQuery(clz, uri);
			break;
		case "delete":
			resp = ThisApplication.context().applications().deleteQuery(clz, uri);
			break;
		case "head":
			// resp = ThisApplication.context().applications().headQuery(clz,
			// uri);
			break;
		case "options":
			// result = this.httpOptions(business, attributes, work, data,
			// invoke);
			break;
		case "patch":
			// result = this.httpPatch(business, attributes, work, data,
			// invoke);
			break;
		case "trace":
			// result = this.httpTrace(business, attributes, work, data,
			// invoke);
			break;
		default:
			throw new Exception("unknown http method " + invoke.getJaxrsMethod());
		}
		/** 进行错误监测 */
		if (!Objects.equals(resp.getType(), ActionResponse.Type.success)) {
			throw new RunningException("invoke url:{} not success, because:{}.", uri, resp.getMessage());
		}
		WrapScriptObject jaxrsResponse = new WrapScriptObject();
		// LinkedHashMap<?, ?> map = resp.getData(LinkedHashMap.class);
		jaxrsResponse.set(gson.toJson(resp.getData()));
		if ((StringUtils.isNotEmpty(invoke.getJaxrsResponseScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxrsResponseScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects,
					new BindingPair("jaxrsResponse", jaxrsResponse));
			scriptHelper.eval(aeiObjects.getWork().getApplication(), invoke.getJaxrsResponseScript(),
					invoke.getJaxrsResponseScriptText());
		}
	}

	private void jaxrsExternal(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String result;
		switch (StringUtils.lowerCase(invoke.getJaxrsMethod())) {
		case "post":
			result = this.httpPost(aeiObjects, invoke);
			break;
		case "put":
			result = this.httpPut(aeiObjects, invoke);
			break;
		case "get":
			result = this.httpGet(aeiObjects, invoke);
			break;
		case "delete":
			result = this.httpDelete(aeiObjects, invoke);
			break;
		case "head":
			result = this.httpHead(aeiObjects, invoke);
			break;
		case "options":
			result = this.httpOptions(aeiObjects, invoke);
			break;
		case "patch":
			result = this.httpPatch(aeiObjects, invoke);
			break;
		case "trace":
			result = this.httpTrace(aeiObjects, invoke);
			break;
		default:
			throw new Exception("unknown http method " + invoke.getJaxrsMethod());
		}
		WrapScriptObject jaxrsResponse = new WrapScriptObject();
		jaxrsResponse.set(result);
		if ((StringUtils.isNotEmpty(invoke.getJaxrsResponseScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxrsResponseScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects,
					new BindingPair("jaxrsResponse", jaxrsResponse));
			scriptHelper.eval(aeiObjects.getWork().getApplication(), invoke.getJaxrsResponseScript(),
					invoke.getJaxrsResponseScriptText());
		}
	}

	private String jaxrsUrl(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String url = invoke.getJaxrsAddress();
		Map<String, String> parameters = new HashMap<>();
		if ((StringUtils.isNotEmpty(invoke.getJaxrsParameterScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxrsParameterScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects,
					new BindingPair("parameters", parameters));
			scriptHelper.eval(aeiObjects.getWork().getApplication(), invoke.getJaxrsParameterScript(),
					invoke.getJaxrsParameterScriptText());
		}
		for (Entry<String, String> entry : parameters.entrySet()) {
			url = StringUtils.replace(url, "{" + entry.getKey() + "}", entry.getValue());
		}
		return url;
	}

	private String jaxrsEvalBody(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		JaxrsBody jaxrsBody = new JaxrsBody();
		if ((StringUtils.isNotEmpty(invoke.getJaxrsBodyScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxrsBodyScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects, new BindingPair("jaxrsBody", jaxrsBody));
			scriptHelper.eval(aeiObjects.getWork().getApplication(), invoke.getJaxrsBodyScript(),
					invoke.getJaxrsBodyScriptText());

		}
		return jaxrsBody.get();
	}

	private String httpPost(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String address = this.jaxrsUrl(aeiObjects, invoke);
		HttpURLConnection connection = this.prepareConnection(address, true, invoke.getJaxrsContentType());
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		Object o = this.jaxrsEvalBody(aeiObjects, invoke);
		connection.connect();
		this.doOutput(connection, o);
		return this.readResultString(connection);
	}

	private String httpPut(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String address = this.jaxrsUrl(aeiObjects, invoke);
		HttpURLConnection connection = this.prepareConnection(address, invoke.getJaxrsWithCipher(),
				invoke.getJaxrsContentType());
		connection.setRequestMethod("PUT");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		Object o = this.jaxrsEvalBody(aeiObjects, invoke);
		connection.connect();
		this.doOutput(connection, o);
		return this.readResultString(connection);
	}

	private String httpGet(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String address = this.jaxrsUrl(aeiObjects, invoke);
		HttpURLConnection connection = this.prepareConnection(address, invoke.getJaxrsWithCipher(),
				invoke.getJaxrsContentType());
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		return this.readResultString(connection);

	}

	private String httpDelete(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String address = this.jaxrsUrl(aeiObjects, invoke);
		HttpURLConnection connection = this.prepareConnection(address, invoke.getJaxrsWithCipher(),
				invoke.getJaxrsContentType());
		connection.setRequestMethod("DELETE");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		return this.readResultString(connection);
	}

	private String httpHead(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String address = this.jaxrsUrl(aeiObjects, invoke);
		HttpURLConnection connection = this.prepareConnection(address, invoke.getJaxrsWithCipher(),
				invoke.getJaxrsContentType());
		connection.setRequestMethod("HEAD");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		return this.readResultString(connection);
	}

	private String httpOptions(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String address = this.jaxrsUrl(aeiObjects, invoke);
		HttpURLConnection connection = this.prepareConnection(address, invoke.getJaxrsWithCipher(),
				invoke.getJaxrsContentType());
		connection.setRequestMethod("OPTIONS");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		return this.readResultString(connection);
	}

	private String httpPatch(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String address = this.jaxrsUrl(aeiObjects, invoke);
		HttpURLConnection connection = this.prepareConnection(address, invoke.getJaxrsWithCipher(),
				invoke.getJaxrsContentType());
		connection.setRequestMethod("PATCH");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		return this.readResultString(connection);
	}

	private String httpTrace(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String address = this.jaxrsUrl(aeiObjects, invoke);
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

		private String get() {
			return Objects.toString(value, "");
		}

		public void set(String value) throws Exception {
			this.value = value;
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

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Invoke invoke) throws Exception {
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Invoke invoke) throws Exception {
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Invoke invoke) throws Exception {
	}

}
