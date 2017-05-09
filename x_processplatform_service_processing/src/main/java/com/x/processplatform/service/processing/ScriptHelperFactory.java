package com.x.processplatform.service.processing;

import java.util.HashMap;
import java.util.Map;

import com.x.organization.core.express.Organization;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;

public class ScriptHelperFactory {

	// private static ScriptHelperFactory INSTANCE;

	public static String initialScriptText;

	private static final String data_binding_name = "data";
	private static final String workContext_binding_name = "workContext";
	private static final String organization_binding_name = "organization";
	private static final String webservicesClient_binding_name = "webservicesClient";
	private static final String dictionary_binding_name = "dictionary";
	private static final String lookup_binding_name = "lookup";

	// public static ScriptHelperFactory instance() throws Exception {
	// if (INSTANCE == null) {
	// synchronized (ScriptHelperFactory.class) {
	// if (INSTANCE == null) {
	// INSTANCE = new ScriptHelperFactory();
	// }
	// }
	// }
	// return INSTANCE;
	// }

	// private ScriptHelperFactory() throws Exception {
	// // String path = ThisApplication.webApplicationDirectory +
	// // "/WEB-INF/classes/META-INF/initialScriptText.js";
	// // this.initialScriptText = FileUtils.readFileToString(new File(path),
	// // Charsets.UTF_8);
	// }

	public static ScriptHelper create(Business business, ProcessingAttributes attributes, Work work, Data data,
			Activity activity, BindingPair... bindingPairs) throws Exception {
		WorkContext workContext = new WorkContext(business, attributes, work, activity);
		Map<String, Object> map = new HashMap<>();
		map.put(workContext_binding_name, workContext);
		map.put(data_binding_name, data);
		map.put(organization_binding_name, new Organization(ThisApplication.context()));
		map.put(webservicesClient_binding_name, new WebservicesClient());
		map.put(dictionary_binding_name,
				new ApplicationDictHelper(business.entityManagerContainer(), work.getApplication()));
		// map.put(lookup_binding_name, new LookupBuilder());
		for (BindingPair o : bindingPairs) {
			map.put(o.getName(), o.getValue());
		}
		return new ScriptHelper(business, map, initialScriptText);

	}
}