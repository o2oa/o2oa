package com.x.processplatform.service.processing.processor.invoke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.Application;
import com.x.base.core.project.x_program_center;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.RunningException;
import com.x.base.core.project.http.ActionResult.Type;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.WrapScriptObject;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class InvokeProcessor extends AbstractInvokeProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvokeProcessor.class);

	public InvokeProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.invokeArrive(aeiObjects.getWork().getActivityToken(), invoke));
		return aeiObjects.getWork();
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.invokeExecute(aeiObjects.getWork().getActivityToken(), invoke));
		List<Work> results = new ArrayList<>();
		boolean passThrough = false;
		switch (invoke.getInvokeMode()) {
		case jaxws:
			// 可以根据返回脚本判断时候流转
			passThrough = this.jaxws(aeiObjects, invoke);
			break;
		case jaxrs:
			// 可以根据返回脚本判断时候流转
			passThrough = this.jaxrs(aeiObjects, invoke);
			break;
		default:
			break;
		}
		if (passThrough) {
			results.add(aeiObjects.getWork());
		} else {
			LOGGER.info("work title:{}, id:{} invoke return false, stay in the current activity.",
					() -> aeiObjects.getWork().getTitle(), () -> aeiObjects.getWork().getId());
		}
		return results;
	}

	@Override
	protected Optional<Route> inquiring(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.invokeInquire(aeiObjects.getWork().getActivityToken(), invoke));
		return aeiObjects.getRoutes().stream().findFirst();
	}

	private boolean jaxws(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		if (BooleanUtils.isTrue(invoke.getInternal())) {
			return this.jaxwsInternal(aeiObjects, invoke);
		} else {
			return this.jaxwsExternal(aeiObjects, invoke);
		}
	}

	private boolean jaxwsInternal(AeiObjects aeiObjects, Invoke invoke) {
		return true;
	}

	private boolean jaxwsExternal(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		Object[] parameters = this.jaxwsEvalParameters(aeiObjects, invoke);
		JaxwsObject jaxwsObject = new JaxwsObject();
		jaxwsObject.setAddress(invoke.getJaxwsAddress());
		jaxwsObject.setMethod(invoke.getJaxwsMethod());
		jaxwsObject.setParameters(parameters);
		boolean passThrough = true;
		if (BooleanUtils.isTrue(invoke.getAsync())) {
			ThisApplication.syncJaxwsInvokeQueue.send(jaxwsObject);
		} else {
			InvokeExecutor executor = new InvokeExecutor();
			Object[] response = executor.execute(jaxwsObject);
			if ((StringUtils.isNotEmpty(invoke.getJaxwsResponseScript()))
					|| (StringUtils.isNotEmpty(invoke.getJaxwsResponseScriptText()))) {
				Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
						aeiObjects.getActivity(), Business.EVENT_INVOKEJAXWSRESPONSE);
				GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
						.putMember(GraalvmScriptingFactory.BINDING_NAME_JAXWSRESPONSE, response);
				Optional<Boolean> opt = GraalvmScriptingFactory.evalAsBoolean(source, bindings);
				if (opt.isPresent() && BooleanUtils.isFalse(opt.get())) {
					passThrough = false;
				}
			}
		}
		return passThrough;
	}

	private Object[] jaxwsEvalParameters(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		List<Object> parameters = new ArrayList<>();
		if ((StringUtils.isNotEmpty(invoke.getJaxwsParameterScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxwsParameterScriptText()))) {
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
					aeiObjects.getActivity(), Business.EVENT_INVOKEJAXWSPARAMETER);
			GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
					.putMember(GraalvmScriptingFactory.BINDING_NAME_JAXWSPARAMETERS, parameters);
			GraalvmScriptingFactory.eval(source, bindings, jsonElement -> {
				JsonArray arr = new JsonArray();
				if (jsonElement.isJsonArray()) {
					arr = jsonElement.getAsJsonArray();
				}
				parameters.clear();
				parameters.addAll(gson.fromJson(arr, new TypeToken<List<Object>>() {
				}.getType()));
			});
		}
		return parameters.toArray();
	}

	private boolean jaxrs(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		if (BooleanUtils.isTrue(invoke.getInternal())) {
			return this.jaxrsInternal(aeiObjects, invoke);
		} else {
			return this.jaxrsExternal(aeiObjects, invoke);
		}
	}

	private boolean jaxrsInternal(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		ActionResponse resp = null;
		switch (StringUtils.upperCase(invoke.getJaxrsMethod())) {
		case ConnectionAction.METHOD_POST:
			resp = jaxrsInternalPost(aeiObjects, invoke);
			break;
		case ConnectionAction.METHOD_PUT:
			resp = jaxrsInternalPut(aeiObjects, invoke);
			break;
		case ConnectionAction.METHOD_GET:
			resp = jaxrsInternalGet(aeiObjects, invoke);
			break;
		case ConnectionAction.METHOD_DELETE:
			resp = jaxrsInternalDelete(aeiObjects, invoke);
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
			throw new ExceptionUnknownHttpMethod(invoke.getJaxrsMethod());
		}
		if ((!BooleanUtils.isTrue(invoke.getAsync()))
				&& ((null == resp) || (!Objects.equals(Type.success, resp.getType())))) {
			LOGGER.warn("invoke not success, work:{}, resp:{}.", aeiObjects.getWork().getId(), resp);
		}
		boolean passThrough = true;
		// 同步执行状态下进行调用判断
		if (!BooleanUtils.isTrue(invoke.getAsync())) {
			WrapScriptObject jaxrsResponse = new WrapScriptObject();
			if (null != resp) {
				jaxrsResponse.type(resp.getType().toString());
				jaxrsResponse.set(gson.toJson(resp.getData()));
			}
			if ((StringUtils.isNotEmpty(invoke.getJaxrsResponseScript()))
					|| (StringUtils.isNotEmpty(invoke.getJaxrsResponseScriptText()))) {
				Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
						aeiObjects.getActivity(), Business.EVENT_INVOKEJAXRSRESPONSE);
				GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
						.putMember(GraalvmScriptingFactory.BINDING_NAME_JAXRSRESPONSE, jaxrsResponse);
				Optional<Boolean> opt = GraalvmScriptingFactory.evalAsBoolean(source, bindings);
				if (opt.isPresent() && BooleanUtils.isFalse(opt.get())) {
					passThrough = false;
				}
			}
		}
		return passThrough;
	}

	private ActionResponse jaxrsInternalDelete(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String uri = this.jaxrsUrl(aeiObjects, invoke);
		if (BooleanUtils.isTrue(invoke.getAsync())) {
			JaxrsObject jaxrsObject = new JaxrsObject();
			if (invoke.getInternalProject().equalsIgnoreCase(x_program_center.class.getSimpleName())
					|| invoke.getInternalProject().equalsIgnoreCase(x_program_center.class.getName())) {
				final Node node = Config.nodes().get(Config.resource_node_centersPirmaryNode());
				if (null != node) {
					String prefix = ThisApplication.context().applications()
							.urlPrefixOfCenterServer(Config.resource_node_centersPirmaryNode(), node.getCenter());
					jaxrsObject.setAddress(StringTools.JoinUrl(prefix + CipherConnectionAction.trim(uri)));
				}
			} else {
				Application application = ThisApplication.context().applications()
						.randomWithWeight(invoke.getInternalProject());
				jaxrsObject.setAddress(
						StringTools.JoinUrl(application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri)));
			}
			jaxrsObject.setInternal(invoke.getInternal());
			jaxrsObject.setMethod(ConnectionAction.METHOD_DELETE);
			jaxrsObject.setContentType(invoke.getJaxrsContentType());
			ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
		} else {
			return ThisApplication.context().applications().deleteQuery(invoke.getInternalProject(), uri);
		}
		return null;
	}

	private ActionResponse jaxrsInternalGet(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String uri = this.jaxrsUrl(aeiObjects, invoke);
		if (BooleanUtils.isTrue(invoke.getAsync())) {
			JaxrsObject jaxrsObject = new JaxrsObject();
			if (invoke.getInternalProject().equalsIgnoreCase(x_program_center.class.getSimpleName())
					|| invoke.getInternalProject().equalsIgnoreCase(x_program_center.class.getName())) {
				final Node node = Config.nodes().get(Config.resource_node_centersPirmaryNode());
				if (null != node) {
					String prefix = ThisApplication.context().applications()
							.urlPrefixOfCenterServer(Config.resource_node_centersPirmaryNode(), node.getCenter());
					jaxrsObject.setAddress(StringTools.JoinUrl(prefix + CipherConnectionAction.trim(uri)));
				}
			} else {
				Application application = ThisApplication.context().applications()
						.randomWithWeight(invoke.getInternalProject());
				jaxrsObject.setAddress(
						StringTools.JoinUrl(application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri)));
			}
			jaxrsObject.setInternal(invoke.getInternal());
			jaxrsObject.setMethod(ConnectionAction.METHOD_GET);
			jaxrsObject.setContentType(invoke.getJaxrsContentType());
			ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			return null;
		} else {
			return ThisApplication.context().applications().getQuery(invoke.getInternalProject(), uri);
		}
	}

	private ActionResponse jaxrsInternalPut(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String body = this.jaxrsEvalBody(aeiObjects, invoke);
		String uri = this.jaxrsUrl(aeiObjects, invoke);
		if (BooleanUtils.isTrue(invoke.getAsync())) {
			JaxrsObject jaxrsObject = new JaxrsObject();
			if (invoke.getInternalProject().equalsIgnoreCase(x_program_center.class.getSimpleName())
					|| invoke.getInternalProject().equalsIgnoreCase(x_program_center.class.getName())) {
				final Node node = Config.nodes().get(Config.resource_node_centersPirmaryNode());
				if (null != node) {
					String prefix = ThisApplication.context().applications()
							.urlPrefixOfCenterServer(Config.resource_node_centersPirmaryNode(), node.getCenter());
					jaxrsObject.setAddress(StringTools.JoinUrl(prefix + CipherConnectionAction.trim(uri)));
				}
			} else {
				Application application = ThisApplication.context().applications()
						.randomWithWeight(invoke.getInternalProject());
				jaxrsObject.setAddress(
						StringTools.JoinUrl(application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri)));
			}
			jaxrsObject.setAddress(uri);
			jaxrsObject.setBody(body);
			jaxrsObject.setInternal(invoke.getInternal());
			jaxrsObject.setMethod(ConnectionAction.METHOD_PUT);
			jaxrsObject.setContentType(invoke.getJaxrsContentType());
			ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			return null;
		} else {
			return ThisApplication.context().applications().putQuery(invoke.getInternalProject(), uri, body);
		}
	}

	private ActionResponse jaxrsInternalPost(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String body = this.jaxrsEvalBody(aeiObjects, invoke);
		String uri = this.jaxrsUrl(aeiObjects, invoke);
		if (BooleanUtils.isTrue(invoke.getAsync())) {
			JaxrsObject jaxrsObject = new JaxrsObject();
			if (invoke.getInternalProject().equalsIgnoreCase(x_program_center.class.getSimpleName())
					|| invoke.getInternalProject().equalsIgnoreCase(x_program_center.class.getName())) {
				final Node node = Config.nodes().get(Config.resource_node_centersPirmaryNode());
				if (null != node) {
					String prefix = ThisApplication.context().applications()
							.urlPrefixOfCenterServer(Config.resource_node_centersPirmaryNode(), node.getCenter());
					jaxrsObject.setAddress(StringTools.JoinUrl(prefix + CipherConnectionAction.trim(uri)));
				}
			} else {
				Application application = ThisApplication.context().applications()
						.randomWithWeight(invoke.getInternalProject());
				jaxrsObject.setAddress(
						StringTools.JoinUrl(application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri)));
			}
			jaxrsObject.setBody(body);
			jaxrsObject.setInternal(invoke.getInternal());
			jaxrsObject.setMethod(ConnectionAction.METHOD_POST);
			jaxrsObject.setContentType(invoke.getJaxrsContentType());
			ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			return null;
		} else {
			return ThisApplication.context().applications().postQuery(invoke.getInternalProject(), uri, body);
		}
	}

	private boolean jaxrsExternal(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String result = "";
		String uri = this.jaxrsUrl(aeiObjects, invoke);
		JaxrsObject jaxrsObject = new JaxrsObject();
		jaxrsObject.setHead(this.jaxrsEvalHeader(aeiObjects, invoke));
		switch (StringUtils.upperCase(invoke.getJaxrsMethod())) {
		case ConnectionAction.METHOD_POST:
			result = jaxrsExternalPost(aeiObjects, invoke, uri, jaxrsObject);
			break;
		case ConnectionAction.METHOD_PUT:
			result = jaxrsExternalPut(aeiObjects, invoke, uri, jaxrsObject);
			break;
		case ConnectionAction.METHOD_GET:
			result = jaxrsExternalGet(aeiObjects, invoke, uri, jaxrsObject);
			break;
		case ConnectionAction.METHOD_DELETE:
			result = jaxrsExternalDelete(aeiObjects, invoke, uri, jaxrsObject);
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
			throw new ExceptionUnknownHttpMethod(invoke.getJaxrsMethod());
		}
		// 同步执行状态下进行调用判断
		if ((!BooleanUtils.isTrue(invoke.getAsync())) && (null == result)) {
			throw new RunningException("invoke address:{} not success, work:{}.", uri, aeiObjects.getWork().getId());
		}
		boolean passThrough = true;
		if (!BooleanUtils.isTrue(invoke.getAsync())) {
			WrapScriptObject jaxrsResponse = new WrapScriptObject();
			if (null == result) {
				jaxrsResponse.type(Type.connectFatal.toString());
			} else {
				jaxrsResponse.type(Type.success.toString());
			}
			jaxrsResponse.set(result);
			if ((StringUtils.isNotEmpty(invoke.getJaxrsResponseScript()))
					|| (StringUtils.isNotEmpty(invoke.getJaxrsResponseScriptText()))) {
				Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
						aeiObjects.getActivity(), Business.EVENT_INVOKEJAXRSRESPONSE);
				GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
						.putMember(GraalvmScriptingFactory.BINDING_NAME_JAXRSRESPONSE, jaxrsResponse);
				Optional<Boolean> opt = GraalvmScriptingFactory.evalAsBoolean(source, bindings);
				if (opt.isPresent() && BooleanUtils.isFalse(opt.get())) {
					passThrough = false;
				}
			}
		}
		return passThrough;
	}

	private String jaxrsExternalDelete(AeiObjects aeiObjects, Invoke invoke, String address, JaxrsObject jaxrsObject)
			throws Exception {
		jaxrsObject.setMethod(ConnectionAction.METHOD_DELETE);
		jaxrsObject.setInternal(false);
		jaxrsObject.setAddress(address);
		jaxrsObject.setContentType(invoke.getJaxrsContentType());
		if (BooleanUtils.isTrue(invoke.getAsync())) {
			ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			return null;
		} else {
			InvokeExecutor executor = new InvokeExecutor();
			return executor.execute(jaxrsObject);
		}
	}

	private String jaxrsExternalGet(AeiObjects aeiObjects, Invoke invoke, String address, JaxrsObject jaxrsObject)
			throws Exception {
		jaxrsObject.setMethod(ConnectionAction.METHOD_GET);
		jaxrsObject.setInternal(false);
		jaxrsObject.setAddress(address);
		jaxrsObject.setContentType(invoke.getJaxrsContentType());
		if (BooleanUtils.isTrue(invoke.getAsync())) {
			ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			return null;
		} else {
			InvokeExecutor executor = new InvokeExecutor();
			return executor.execute(jaxrsObject);
		}
	}

	private String jaxrsExternalPut(AeiObjects aeiObjects, Invoke invoke, String address, JaxrsObject jaxrsObject)
			throws Exception {
		String body = this.jaxrsEvalBody(aeiObjects, invoke);
		jaxrsObject.setMethod(ConnectionAction.METHOD_PUT);
		jaxrsObject.setInternal(false);
		jaxrsObject.setAddress(address);
		jaxrsObject.setBody(body);
		jaxrsObject.setContentType(invoke.getJaxrsContentType());
		if (BooleanUtils.isTrue(invoke.getAsync())) {
			ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			return null;
		} else {
			InvokeExecutor executor = new InvokeExecutor();
			return executor.execute(jaxrsObject);
		}
	}

	private String jaxrsExternalPost(AeiObjects aeiObjects, Invoke invoke, String address, JaxrsObject jaxrsObject)
			throws Exception {
		String body = this.jaxrsEvalBody(aeiObjects, invoke);
		jaxrsObject.setMethod(ConnectionAction.METHOD_POST);
		jaxrsObject.setInternal(false);
		jaxrsObject.setAddress(address);
		jaxrsObject.setBody(body);
		jaxrsObject.setContentType(invoke.getJaxrsContentType());
		if (BooleanUtils.isTrue(invoke.getAsync())) {
			ThisApplication.syncJaxrsInvokeQueue.send(jaxrsObject);
			return null;
		} else {
			InvokeExecutor executor = new InvokeExecutor();
			return executor.execute(jaxrsObject);
		}
	}

	private String jaxrsUrl(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		String url = invoke.getJaxrsAddress();
		Map<String, String> parameters = new HashMap<>();
		if ((StringUtils.isNotEmpty(invoke.getJaxrsParameterScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxrsParameterScriptText()))) {
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
					aeiObjects.getActivity(), Business.EVENT_INVOKEJAXRSPARAMETER);
			GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
					.putMember(GraalvmScriptingFactory.BINDING_NAME_JAXRSPARAMETERS, parameters);
			// map有可能返回null
			Map<String, String> map = GraalvmScriptingFactory.eval(source, bindings,
					new TypeToken<Map<String, String>>() {
					}.getType());
			if (null != map) {
				parameters.putAll(map);
			}
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
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
					aeiObjects.getActivity(), Business.EVENT_INVOKEJAXRSBODY);
			GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
					.putMember(GraalvmScriptingFactory.BINDING_NAME_JAXRSBODY, jaxrsBody);
			GraalvmScriptingFactory.eval(source, bindings, jsonElement -> {
				if (Objects.nonNull(jsonElement) && (!jsonElement.isJsonNull())) {
					jaxrsBody.set(gson.toJson(jsonElement));
				}
			});
		}
		return jaxrsBody.get();
	}

	private Map<String, String> jaxrsEvalHeader(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		Map<String, String> map = new LinkedHashMap<>();
		if ((StringUtils.isNotEmpty(invoke.getJaxrsHeadScript()))
				|| (StringUtils.isNotEmpty(invoke.getJaxrsHeadScriptText()))) {
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
					aeiObjects.getActivity(), Business.EVENT_INVOKEJAXRSHEAD);
			GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
					.putMember(GraalvmScriptingFactory.BINDING_NAME_JAXRSHEADERS, map);
			map.putAll(GraalvmScriptingFactory.eval(source, bindings, new TypeToken<Map<String, String>>() {
			}.getType()));
		}
		return map;
	}

	public class JaxrsBody {

		private String value;

		private String get() {
			return Objects.toString(value, "");
		}

		public void set(String value) {
			this.value = value;
		}

	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		// nothing
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Invoke invoke, List<Work> works) throws Exception {
		// nothing
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Invoke invoke) throws Exception {
		// nothing
	}

}
