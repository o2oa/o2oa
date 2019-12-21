package com.x.processplatform.service.processing.processor.manual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.processor.AeiObjects;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * 在Manual环节计算所有的待办人的Identity
 * 
 * @author Rui
 *
 */
@SuppressWarnings("restriction")
public class TranslateTaskIdentityTools {

	private static Logger logger = LoggerFactory.getLogger(TranslateTaskIdentityTools.class);

	/* 计算manual节点中所有的待办，全部翻译成Identity */
	public static TaskIdentities translate(AeiObjects aeiObjects, Manual manual) throws Exception {
		TaskIdentities taskIdentities = new TaskIdentities();
		List<String> units = new ArrayList<>();
		List<String> groups = new ArrayList<>();
		/* 指定的身份 */
		if (ListTools.isNotEmpty(manual.getTaskIdentityList())) {
			taskIdentities.addIdentities(manual.getTaskIdentityList());
		}
		/* 选择了职务 */
		taskIdentities.addIdentities(duty(aeiObjects, manual));
		/* 指定data数据路径值 */
		data(taskIdentities, units, groups, aeiObjects.getData(), manual);
		/* 使用脚本计算 */
		script(taskIdentities, units, groups, aeiObjects, manual);
		/* 指定处理组织 */
		if (ListTools.isNotEmpty(manual.getTaskUnitList())) {
			units.addAll(manual.getTaskUnitList());
		}
		/* 指定处理群组 */
		if (ListTools.isNotEmpty(manual.getTaskGroupList())) {
			groups.addAll(manual.getTaskGroupList());
		}
		if (ListTools.isNotEmpty(units)) {
			taskIdentities.addIdentities(aeiObjects.business().organization().identity().listWithUnitSubDirect(units));
		}
		if (ListTools.isNotEmpty(groups)) {
			taskIdentities.addIdentities(aeiObjects.business().organization().identity().listWithGroup(groups));
		}
		return taskIdentities;
	}

	/* 取到指定职务的identity */
	private static List<String> duty(AeiObjects aeiObjects, Manual manual) throws Exception {
		List<String> list = new ArrayList<>();
		if (StringUtils.isNotEmpty(manual.getTaskDuty())) {
			JsonArray array = XGsonBuilder.instance().fromJson(manual.getTaskDuty(), JsonArray.class);
			Iterator<JsonElement> iterator = array.iterator();
			while (iterator.hasNext()) {
				JsonObject o = iterator.next().getAsJsonObject();
				String name = o.get("name").getAsString();
				ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects);
				List<String> ds = scriptHelper.evalExtrectDistinguishedName(aeiObjects.getWork().getApplication(), null,
						o.get("code").getAsString());
				if (ListTools.isNotEmpty(ds)) {
					for (String str : ds) {
						List<String> os = aeiObjects.business().organization().unitDuty()
								.listIdentityWithUnitWithName(str, name);
						if (ListTools.isNotEmpty(os)) {
							list.addAll(os);
						}
					}
				}
			}
		}
		return ListTools.trim(list, true, true);
	}

	/* 取到script指定的identity */
	@SuppressWarnings("restriction")
	private static List<String> script(TaskIdentities taskIdentities, List<String> units, List<String> groups,
			AeiObjects aeiObjects, Manual manual) throws Exception {
		List<String> list = new ArrayList<>();
		if ((StringUtils.isNotEmpty(manual.getTaskScript())) || (StringUtils.isNotEmpty(manual.getTaskScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects);
			Object o = scriptHelper.eval(aeiObjects.getWork().getApplication(), manual.getTaskScript(),
					manual.getTaskScriptText());
			if (null != o) {
				if (o instanceof CharSequence) {
					taskIdentities.addIdentity(o.toString());
				} else if (o instanceof JsonObject) {
					JsonObject jsonObject = (JsonObject) o;
					addJsonObjectToTaskIdentities(taskIdentities, units, groups, jsonObject);
				} else if (o instanceof JsonArray) {
					for (JsonElement jsonElement : (JsonArray) o) {
						if (jsonElement.isJsonObject()) {
							JsonObject jsonObject = jsonElement.getAsJsonObject();
							addJsonObjectToTaskIdentities(taskIdentities, units, groups, jsonObject);
						}
					}
				} else if (o instanceof Iterable) {
					for (Object obj : (Iterable<?>) o) {
						if (null != obj) {
							if (obj instanceof CharSequence) {
								taskIdentities.addIdentity(Objects.toString(obj, ""));
							} else {
								addObjectToTaskIdentities(taskIdentities, units, groups, obj);
							}
						}
					}
				} else if (o instanceof ScriptObjectMirror) {
					ScriptObjectMirror som = (ScriptObjectMirror) o;
					if (som.isArray()) {
						Object[] objs = (som.to(Object[].class));
						for (Object obj : objs) {
							if (null != obj) {
								if (obj instanceof CharSequence) {
									taskIdentities.addIdentity(Objects.toString(obj, ""));
								} else {
									addObjectToTaskIdentities(taskIdentities, units, groups, obj);
								}
							}
						}
					} else {
						addObjectToTaskIdentities(taskIdentities, units, groups, som);
					}
				}
			}
		}
		return list;
	}

	/* 取得通过路径指定的identity */
	private static void data(TaskIdentities taskIdentities, List<String> units, List<String> groups, Data data,
			Manual manual) throws Exception {
		if (ListTools.isNotEmpty(manual.getTaskDataPathList())) {
			for (String str : ListTools.trim(manual.getTaskDataPathList(), true, true)) {
				Object o = data.find(str);
				if (null != o) {
					if (o instanceof CharSequence) {
						taskIdentities.addIdentity(o.toString());
					} else if (o instanceof Iterable) {
						for (Object v : (Iterable<?>) o) {
							if (null != v) {
								if ((v instanceof CharSequence)) {
									taskIdentities.addIdentity(v.toString());
								} else {
									addObjectToTaskIdentities(taskIdentities, units, groups, v);
								}
							}
						}
					} else {
						addObjectToTaskIdentities(taskIdentities, units, groups, o);
					}
				}
			}
		}

	}

	private static void addObjectToTaskIdentities(TaskIdentities taskIdentities, List<String> units,
			List<String> groups, Object o) throws Exception {
		String d = Objects.toString(PropertyUtils.getProperty(o, JpaObject.DISTINGUISHEDNAME), "");
		if (OrganizationDefinition.isIdentityDistinguishedName(d)) {
			Boolean ignore = BooleanUtils.isTrue(BooleanUtils.toBooleanObject(
					Objects.toString(PropertyUtils.getProperty(o, TaskIdentity.IGNOREEMPOWER), "false")));
			TaskIdentity taskIdentity = new TaskIdentity();
			taskIdentity.setIdentity(d);
			taskIdentity.setIgnoreEmpower(ignore);
			taskIdentities.add(taskIdentity);
		} else if (OrganizationDefinition.isUnitDistinguishedName(d)) {
			units.add(d);
		} else if (OrganizationDefinition.isGroupDistinguishedName(d)) {
			units.add(d);
		}
	}

	private static void addJsonObjectToTaskIdentities(TaskIdentities taskIdentities, List<String> units,
			List<String> group, JsonObject jsonObject) throws Exception {
		if (jsonObject.has(JpaObject.DISTINGUISHEDNAME)) {
			String d = jsonObject.get(JpaObject.DISTINGUISHEDNAME).getAsString();
			if (OrganizationDefinition.isIdentityDistinguishedName(d)) {
				boolean ignore = false;
				if (jsonObject.has(TaskIdentity.IGNOREEMPOWER)) {
					ignore = BooleanUtils.isTrue(
							BooleanUtils.toBooleanObject(jsonObject.get(TaskIdentity.IGNOREEMPOWER).getAsString()));
				}
				TaskIdentity taskIdentity = new TaskIdentity();
				taskIdentity.setIdentity(d);
				taskIdentity.setIgnoreEmpower(ignore);
				taskIdentities.add(taskIdentity);
			} else if (OrganizationDefinition.isUnitDistinguishedName(d)) {
				units.add(d);
			} else if (OrganizationDefinition.isGroupDistinguishedName(d)) {
				units.add(d);
			}
		}
	}
}