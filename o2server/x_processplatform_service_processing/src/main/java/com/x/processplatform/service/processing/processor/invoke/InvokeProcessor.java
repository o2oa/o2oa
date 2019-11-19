package com.x.processplatform.service.processing.processor.invoke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.Application;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.RunningException;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.ScriptingEngine;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.BindingPair;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.ThisApplication;
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
		JaxwsObject jaxwsObject = new JaxwsObject();
		jaxwsObject.setAddress(invoke.getJaxwsAddress());
		jaxwsObject.setMethod(invoke.getJaxwsMethod());
		jaxwsObject.setParameters(parameters);
		if (BooleanUtils.isTrue(invoke.getAsync())) {
			ThisApplication.syncJaxwsInvokeQueue.send(jaxwsObject);
		} else {
			InvokeExecutor executor = new InvokeExecutor();
			Object response = executor.execute(jaxwsObject);
			if ((StringUtils.isNotEmpty(invoke.getJaxwsResponseScript()))
					|| (StringUtils.isNotEmpty(invoke.getJaxwsResponseScriptText()))) {
				ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects,
						new BindingPair(ScriptingEngine.BINDINGNAME_JAXWSRESPONSE, response));
				scriptHelper.eval(aeiObjects.getWork().getApplication(), invoke.getJaxwsResponseScript(),
						invoke.getJaxwsResponseScriptText());
			}
		}
	}

	private Object[] jaxwsEvalParameters(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		List<?> parameters = new ArrayList<>();
		if ((StringUtils.isNotEmpty(invoke.getJaxwsParameterScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxwsParameterScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects,
					new BindingPair(ScriptingEngine.BINDINGNAME_PARAMETERS, parameters));
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
		String body = "";
		switch (StringUtils.lowerCase(invoke.getJaxrsMethod())) {
		case "post":
			body = this.jaxrsEvalBody(aeiObjects, invoke);
			if (BooleanUtils.isTrue(invoke.getAsync())) {
				JaxrsObject jaxrsObject = new JaxrsObject();
				Application application = ThisApplication.context().applications().randomWithWeight(clz.getName());
				jaxrsObject.setAddress(
						StringTools.JoinUrl(application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri)));
				jaxrsObject.setBody(body);
				jaxrsObject.setInternal(invoke.getInternal());
				jaxrsObject.setMethod("post");
				jaxrsObject.setContentType(invoke.getJaxrsContentType());
				ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			} else {
				resp = ThisApplication.context().applications().postQuery(clz, uri, body);
			}
			break;
		case "put":
			body = this.jaxrsEvalBody(aeiObjects, invoke);
			if (BooleanUtils.isTrue(invoke.getAsync())) {
				JaxrsObject jaxrsObject = new JaxrsObject();
				Application application = ThisApplication.context().applications().randomWithWeight(clz.getName());
				jaxrsObject.setAddress(
						StringTools.JoinUrl(application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri)));
				jaxrsObject.setBody(body);
				jaxrsObject.setInternal(invoke.getInternal());
				jaxrsObject.setMethod("put");
				jaxrsObject.setContentType(invoke.getJaxrsContentType());
				ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			} else {
				resp = ThisApplication.context().applications().putQuery(clz, uri, body);
			}
			break;
		case "get":
			if (BooleanUtils.isTrue(invoke.getAsync())) {
				JaxrsObject jaxrsObject = new JaxrsObject();
				Application application = ThisApplication.context().applications().randomWithWeight(clz.getName());
				jaxrsObject.setAddress(
						StringTools.JoinUrl(application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri)));
				jaxrsObject.setInternal(invoke.getInternal());
				jaxrsObject.setMethod("get");
				jaxrsObject.setContentType(invoke.getJaxrsContentType());
				ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			} else {
				resp = ThisApplication.context().applications().getQuery(clz, uri);
			}
			break;
		case "delete":
			if (BooleanUtils.isTrue(invoke.getAsync())) {
				JaxrsObject jaxrsObject = new JaxrsObject();
				Application application = ThisApplication.context().applications().randomWithWeight(clz.getName());
				jaxrsObject.setAddress(
						StringTools.JoinUrl(application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri)));
				jaxrsObject.setInternal(invoke.getInternal());
				jaxrsObject.setMethod("delete");
				jaxrsObject.setContentType(invoke.getJaxrsContentType());
				ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			} else {
				resp = ThisApplication.context().applications().deleteQuery(clz, uri);
			}
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
			throw new Exception("unknown http method " + invoke.getJaxrsMethod());
		}
		/** 进行错误监测 */
		if (!BooleanUtils.isTrue(invoke.getAsync())) {
			if (!Objects.equals(resp.getType(), ActionResponse.Type.success)) {
				throw new RunningException("invoke url:{} not success, because:{}, work:{}.", uri, resp.getMessage(),
						aeiObjects.getWork().getId());
			}
			WrapScriptObject jaxrsResponse = new WrapScriptObject();
			// LinkedHashMap<?, ?> map = resp.getData(LinkedHashMap.class);
			jaxrsResponse.set(gson.toJson(resp.getData()));
			if ((StringUtils.isNotEmpty(invoke.getJaxrsResponseScript()))
					|| (StringUtils.isNotEmpty(invoke.getJaxrsResponseScriptText()))) {
				ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects,
						new BindingPair(ScriptingEngine.BINDINGNAME_JAXRSRESPONSE, jaxrsResponse));
				scriptHelper.eval(aeiObjects.getWork().getApplication(), invoke.getJaxrsResponseScript(),
						invoke.getJaxrsResponseScriptText());
			}
		}
	}

	private void jaxrsExternal(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String result = "";
		String address = this.jaxrsUrl(aeiObjects, invoke);
		String body = "";
		JaxrsObject jaxrsObject = new JaxrsObject();
		jaxrsObject.setHead(this.jaxrsEvalHead(aeiObjects, invoke));
		switch (StringUtils.lowerCase(invoke.getJaxrsMethod())) {
		case "post":
			body = this.jaxrsEvalBody(aeiObjects, invoke);
			jaxrsObject.setMethod("post");
			jaxrsObject.setInternal(false);
			jaxrsObject.setAddress(address);
			jaxrsObject.setBody(body);
			jaxrsObject.setContentType(invoke.getJaxrsContentType());
			if (BooleanUtils.isTrue(invoke.getAsync())) {
				ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			} else {
				InvokeExecutor executor = new InvokeExecutor();
				result = executor.execute(jaxrsObject);
			}
			break;
		case "put":
			body = this.jaxrsEvalBody(aeiObjects, invoke);
			jaxrsObject.setMethod("put");
			jaxrsObject.setInternal(false);
			jaxrsObject.setAddress(address);
			jaxrsObject.setBody(body);
			jaxrsObject.setContentType(invoke.getJaxrsContentType());
			if (BooleanUtils.isTrue(invoke.getAsync())) {
				ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			} else {
				InvokeExecutor executor = new InvokeExecutor();
				result = executor.execute(jaxrsObject);
			}
			break;
		case "get":
			jaxrsObject.setMethod("get");
			jaxrsObject.setInternal(false);
			jaxrsObject.setAddress(address);
			jaxrsObject.setContentType(invoke.getJaxrsContentType());
			if (BooleanUtils.isTrue(invoke.getAsync())) {
				ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			} else {
				InvokeExecutor executor = new InvokeExecutor();
				result = executor.execute(jaxrsObject);
			}
			break;
		case "delete":
			jaxrsObject.setMethod("delete");
			jaxrsObject.setInternal(false);
			jaxrsObject.setAddress(address);
			jaxrsObject.setContentType(invoke.getJaxrsContentType());
			if (BooleanUtils.isTrue(invoke.getAsync())) {
				ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			} else {
				InvokeExecutor executor = new InvokeExecutor();
				result = executor.execute(jaxrsObject);
			}
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
			throw new Exception("unknown http method: " + invoke.getJaxrsMethod());
		}
		if (!BooleanUtils.isTrue(invoke.getAsync())) {
			WrapScriptObject jaxrsResponse = new WrapScriptObject();
			jaxrsResponse.set(result);
			if ((StringUtils.isNotEmpty(invoke.getJaxrsResponseScript()))
					|| (StringUtils.isNotEmpty(invoke.getJaxrsResponseScriptText()))) {
				ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects,
						new BindingPair(ScriptingEngine.BINDINGNAME_JAXRSRESPONSE, jaxrsResponse));
				scriptHelper.eval(aeiObjects.getWork().getApplication(), invoke.getJaxrsResponseScript(),
						invoke.getJaxrsResponseScriptText());
			}
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

	private Map<String, String> jaxrsEvalHead(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		Map<String, String> map = new LinkedHashMap<>();
		if ((StringUtils.isNotEmpty(invoke.getJaxrsHeadScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxrsHeadScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects, new BindingPair("jaxrsHead", map));
			scriptHelper.eval(aeiObjects.getWork().getApplication(), invoke.getJaxrsHeadScript(),
					invoke.getJaxrsHeadScriptText());
		}
		return map;
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
