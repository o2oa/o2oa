package com.x.processplatform.service.processing;

import java.util.HashMap;
import java.util.Map;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.scripting.ScriptingEngine;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class ScriptHelperFactory {

	public static String initialScriptText;

	public static ScriptHelper create(AeiObjects aeiObjects, BindingPair... bindingPairs) throws Exception {
		WorkContext workContext = new WorkContext(aeiObjects);
		Map<String, Object> map = new HashMap<>();
		map.put(ScriptingEngine.BINDINGNAME_WORKCONTEXT, workContext);
		map.put(ScriptingEngine.BINDINGNAME_GSON, XGsonBuilder.instance());
		map.put(ScriptingEngine.BINDINGNAME_DATA, aeiObjects.getData());
		map.put(ScriptingEngine.BINDINGNAME_ORGANIZATION, new Organization(ThisApplication.context()));
		map.put(ScriptingEngine.BINDINGNAME_WEBSERVICESCLIENT, new WebservicesClient());
		map.put(ScriptingEngine.BINDINGNAME_DICTIONARY,
				new ApplicationDictHelper(aeiObjects.entityManagerContainer(), aeiObjects.getWork().getApplication()));
		map.put(ScriptingEngine.BINDINGNAME_APPLICATIONS, ThisApplication.context().applications());
		if ((null != aeiObjects.getProcessingAttributes())
				&& (null != aeiObjects.getProcessingAttributes().getRouteData())) {
			map.put(ScriptingEngine.BINDINGNAME_ROUTEDATA,
					aeiObjects.getProcessingAttributes().getRouteData().toString());
		} else {
			map.put(ScriptingEngine.BINDINGNAME_ROUTEDATA, "");
		}
		for (BindingPair o : bindingPairs) {
			map.put(o.getName(), o.getValue());
		}
		ScriptHelper sh = new ScriptHelper(aeiObjects.business(), map, initialScriptText);
		sh.setAeiObjects(aeiObjects);
		return sh;
	}

	public static ScriptHelper createWithTask(Business business, Work work, Data data, Activity activity, Task task,
			BindingPair... bindingPairs) throws Exception {
		WorkContext workContext = new WorkContext(business, work, activity, task);
		Map<String, Object> map = new HashMap<>();
		map.put(ScriptingEngine.BINDINGNAME_WORKCONTEXT, workContext);
		map.put(ScriptingEngine.BINDINGNAME_DATA, data);
		map.put(ScriptingEngine.BINDINGNAME_ORGANIZATION, new Organization(ThisApplication.context()));
		map.put(ScriptingEngine.BINDINGNAME_WEBSERVICESCLIENT, new WebservicesClient());
		map.put(ScriptingEngine.BINDINGNAME_DICTIONARY,
				new ApplicationDictHelper(business.entityManagerContainer(), work.getApplication()));
		map.put(ScriptingEngine.BINDINGNAME_APPLICATIONS, ThisApplication.context().applications());
		for (BindingPair o : bindingPairs) {
			map.put(o.getName(), o.getValue());
		}
		ScriptHelper sh = new ScriptHelper(business, map, initialScriptText);
		return sh;
	}

	public static ScriptHelper createWithTaskCompleted(Business business, Work work, Data data, Activity activity,
			TaskCompleted taskCompleted, BindingPair... bindingPairs) throws Exception {
		WorkContext workContext = new WorkContext(business, work, activity, taskCompleted);
		Map<String, Object> map = new HashMap<>();
		map.put(ScriptingEngine.BINDINGNAME_WORKCONTEXT, workContext);
		map.put(ScriptingEngine.BINDINGNAME_DATA, data);
		map.put(ScriptingEngine.BINDINGNAME_ORGANIZATION, new Organization(ThisApplication.context()));
		map.put(ScriptingEngine.BINDINGNAME_WEBSERVICESCLIENT, new WebservicesClient());
		map.put(ScriptingEngine.BINDINGNAME_DICTIONARY,
				new ApplicationDictHelper(business.entityManagerContainer(), work.getApplication()));
		map.put(ScriptingEngine.BINDINGNAME_APPLICATIONS, ThisApplication.context().applications());
		for (BindingPair o : bindingPairs) {
			map.put(o.getName(), o.getValue());
		}
		ScriptHelper sh = new ScriptHelper(business, map, initialScriptText);
		return sh;
	}
}