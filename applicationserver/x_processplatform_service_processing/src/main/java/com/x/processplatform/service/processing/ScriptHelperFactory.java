package com.x.processplatform.service.processing;

import java.util.HashMap;
import java.util.Map;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.organization.core.express.Organization;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class ScriptHelperFactory {

	public static String initialScriptText;

	private static final String data_binding_name = "data";
	private static final String workContext_binding_name = "workContext";
	private static final String organization_binding_name = "organization";
	private static final String webservicesClient_binding_name = "webservicesClient";
	private static final String dictionary_binding_name = "dictionary";
	private static final String lookup_binding_name = "lookup";
	private static final String applications_binding_name = "applications";

	// public static ScriptHelper create(AeiObjects aeiObjects, BindingPair...
	// bindingPairs) throws Exception {
	// WorkContext workContext = new WorkContext(aeiObjects);
	// Map<String, Object> map = new HashMap<>();
	// map.put(workContext_binding_name, workContext);
	// map.put(data_binding_name, aeiObjects.getData());
	// map.put(organization_binding_name, new
	// Organization(ThisApplication.context()));
	// map.put(webservicesClient_binding_name, new WebservicesClient());
	// map.put(dictionary_binding_name, new
	// ApplicationDictHelper(aeiObjects.getBusiness().entityManagerContainer(),
	// aeiObjects.getWork().getApplication()));
	// map.put(aeiObjects, ThisApplication.context().applications());
	// for (BindingPair o : bindingPairs) {
	// map.put(o.getName(), o.getValue());
	// }
	// ScriptHelper sh = new ScriptHelper(aeiObjects.getBusiness(), map,
	// initialScriptText);
	// sh.setAeiObjects(aeiObjects);
	// return sh;
	// }

	public static ScriptHelper create(AeiObjects aeiObjects, BindingPair... bindingPairs) throws Exception {
		WorkContext workContext = new WorkContext(aeiObjects);
		Map<String, Object> map = new HashMap<>();
		map.put(workContext_binding_name, workContext);
		map.put("gson", XGsonBuilder.instance());
		map.put(data_binding_name, aeiObjects.getData());
		map.put(organization_binding_name, new Organization(ThisApplication.context()));
		map.put(webservicesClient_binding_name, new WebservicesClient());
		map.put(dictionary_binding_name,
				new ApplicationDictHelper(aeiObjects.entityManagerContainer(), aeiObjects.getWork().getApplication()));
		map.put(applications_binding_name, ThisApplication.context().applications());
		for (BindingPair o : bindingPairs) {
			map.put(o.getName(), o.getValue());
		}
		ScriptHelper sh = new ScriptHelper(aeiObjects.business(), map, initialScriptText);
		sh.setAeiObjects(aeiObjects);
		return sh;
	}

	// public static ScriptHelper create(AeiObjects aeiObjects, BindingPair...
	// bindingPairs) throws Exception {
	// WorkContext workContext = new WorkContext(aeiObjects);
	// Map<String, Object> map = new HashMap<>();
	// map.put(workContext_binding_name, workContext);
	// map.put(data_binding_name, aeiObjects.getData());
	// map.put(organization_binding_name, new
	// Organization(ThisApplication.context()));
	// map.put(webservicesClient_binding_name, new WebservicesClient());
	// map.put(dictionary_binding_name, new ApplicationDictHelper(
	// aeiObjects.getBusiness().entityManagerContainer(),
	// aeiObjects.getWork().getApplication()));
	// map.put(applications_binding_name, ThisApplication.context().applications());
	// for (BindingPair o : bindingPairs) {
	// map.put(o.getName(), o.getValue());
	// }
	// ScriptHelper sh = new ScriptHelper(aeiObjects.getBusiness(), map,
	// initialScriptText);
	// return sh;
	// }

	/**
	 * 用于单独生成脚本运行环境,ManualBeforeTaskScript,ManualAfterTaskScript
	 * 
	 * @param business
	 * @param work
	 * @param data
	 * @param activity
	 * @param bindingPairs
	 * @return
	 * @throws Exception
	 */
	public static ScriptHelper create(Business business, Work work, Data data, Activity activity,
			BindingPair... bindingPairs) throws Exception {
		WorkContext workContext = new WorkContext(business, work, activity);
		Map<String, Object> map = new HashMap<>();
		map.put(workContext_binding_name, workContext);
		map.put(data_binding_name, data);
		map.put(organization_binding_name, new Organization(ThisApplication.context()));
		map.put(webservicesClient_binding_name, new WebservicesClient());
		map.put(dictionary_binding_name,
				new ApplicationDictHelper(business.entityManagerContainer(), work.getApplication()));
		map.put(applications_binding_name, ThisApplication.context().applications());
		for (BindingPair o : bindingPairs) {
			map.put(o.getName(), o.getValue());
		}
		ScriptHelper sh = new ScriptHelper(business, map, initialScriptText);
		return sh;

	}
}