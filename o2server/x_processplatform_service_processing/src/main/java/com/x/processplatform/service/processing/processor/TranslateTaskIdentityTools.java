package com.x.processplatform.service.processing.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.express.Organization.ClassifyDistinguishedName;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;

/**
 * 在Manual环节计算所有的待办人的Identity
 * 
 * @author Rui
 *
 */
public class TranslateTaskIdentityTools {

	private static Logger logger = LoggerFactory.getLogger(TranslateTaskIdentityTools.class);

	/* 计算manual节点中所有的待办，全部翻译成Identity */
	public static List<String> translate(AeiObjects aeiObjects, Manual manual) throws Exception {
		List<String> identities = new ArrayList<>();
		List<String> units = new ArrayList<>();
		List<String> groups = new ArrayList<>();
		ClassifyDistinguishedName classifyDistinguishedName = null;
		/* 指定的身份 */
		if (ListTools.isNotEmpty(manual.getTaskIdentityList())) {
			identities.addAll(manual.getTaskIdentityList());
		}
		/* 选择了职务 */
		identities.addAll(duty(aeiObjects, manual));
		/* 指定data数据路径值 */
		classifyDistinguishedName = aeiObjects.business().organization()
				.classifyDistinguishedNames(data(aeiObjects.business(), aeiObjects.getData(), manual));
		identities.addAll(classifyDistinguishedName.getIdentityList());
		units.addAll(classifyDistinguishedName.getUnitList());
		groups.addAll(classifyDistinguishedName.getGroupList());
		/* 使用脚本计算 */
		classifyDistinguishedName = aeiObjects.business().organization()
				.classifyDistinguishedNames(script(aeiObjects, manual));
		identities.addAll(classifyDistinguishedName.getIdentityList());
		units.addAll(classifyDistinguishedName.getUnitList());
		groups.addAll(classifyDistinguishedName.getGroupList());
		/* 指定处理组织 */
		if (ListTools.isNotEmpty(manual.getTaskUnitList())) {
			units.addAll(manual.getTaskUnitList());
		}
		/* 指定处理群组 */
		if (ListTools.isNotEmpty(manual.getTaskGroupList())) {
			groups.addAll(manual.getTaskGroupList());
		}
		identities.addAll(aeiObjects.business().organization().identity().listWithGroup(groups));
		identities.addAll(aeiObjects.business().organization().identity().listWithUnitSubDirect(units));
		identities = ListTools.trim(identities, true, true);
		logger.debug("work title:{}, id:{}, manual name:{}, id:{}, translate task identity: {}",
				aeiObjects.getWork().getTitle(), aeiObjects.getWork().getId(), manual.getName(), manual.getId(),
				XGsonBuilder.toJson(identities));
		List<String> os = aeiObjects.business().organization().identity().list(identities);
		if (os.size() != identities.size()) {
			logger.warn(
					"work title:{}, id:{}, manual name:{}, id:{}, translate task identity: {}, result not with same length: {}.",
					aeiObjects.getWork().getTitle(), aeiObjects.getWork().getId(), manual.getName(), manual.getId(),
					XGsonBuilder.toJson(identities), XGsonBuilder.toJson(os));
		}
		return os;
	}

	/** 取到指定职务的identity */
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

	/** 取到script指定的identity */
	private static List<String> script(AeiObjects aeiObjects, Manual manual) throws Exception {
		List<String> list = new ArrayList<>();
		if ((StringUtils.isNotEmpty(manual.getTaskScript())) || (StringUtils.isNotEmpty(manual.getTaskScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects);
			List<String> os = scriptHelper.evalExtrectDistinguishedName(aeiObjects.getWork().getApplication(),
					manual.getTaskScript(), manual.getTaskScriptText());
			if (ListTools.isNotEmpty(os)) {
				list.addAll(os);
			}
		}
		return list;
	}

	/** 取得通过路径指定的identity */
	private static List<String> data(Business business, Data data, Manual manual) throws Exception {
		List<String> list = new ArrayList<>();
		if (ListTools.isNotEmpty(manual.getTaskDataPathList())) {
			for (String str : ListTools.trim(manual.getTaskDataPathList(), true, true)) {
				if (StringUtils.isNotEmpty(str)) {
					list.addAll(data.extractDistinguishedName(str));
				}
			}
		}
		return list;
	}

}
