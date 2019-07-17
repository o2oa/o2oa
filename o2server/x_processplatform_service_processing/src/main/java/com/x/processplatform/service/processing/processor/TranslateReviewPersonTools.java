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
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;

/**
 * 在Manual环节计算所有的参阅人的Identity
 * 
 * @author Rui
 *
 */
public class TranslateReviewPersonTools {

	private static Logger logger = LoggerFactory.getLogger(TranslateReviewPersonTools.class);

	/* 计算参阅人，全部翻译成Identity,最终全部转换为person */
	public static List<String> translate(AeiObjects aeiObjects) throws Exception {
		List<String> identities = new ArrayList<>();
		List<String> units = new ArrayList<>();
		List<String> groups = new ArrayList<>();
		/* 指定的身份 */
		if (ListTools.isNotEmpty(aeiObjects.getActivity().getReviewIdentityList())) {
			identities.addAll(aeiObjects.getActivity().getReviewIdentityList());
		}
		/* 选择了职务 */
		identities.addAll(duty(aeiObjects));
		ClassifyDistinguishedName classifyDistinguishedName = null;
		/* 指定data数据路径值 */
		classifyDistinguishedName = aeiObjects.business().organization().classifyDistinguishedNames(
				data(aeiObjects.business(), aeiObjects.getData(), aeiObjects.getActivity()));
		identities.addAll(classifyDistinguishedName.getIdentityList());
		units.addAll(classifyDistinguishedName.getUnitList());
		groups.addAll(classifyDistinguishedName.getGroupList());
		/* 使用脚本计算 */
		classifyDistinguishedName = aeiObjects.business().organization().classifyDistinguishedNames(script(aeiObjects));
		identities.addAll(classifyDistinguishedName.getIdentityList());
		units.addAll(classifyDistinguishedName.getUnitList());
		groups.addAll(classifyDistinguishedName.getGroupList());
		/* 指定处理组织 */
		if (ListTools.isNotEmpty(aeiObjects.getActivity().getReviewUnitList())) {
			units.addAll(aeiObjects.getActivity().getReviewUnitList());
		}
		/* 指定处理群组 */
		if (ListTools.isNotEmpty(aeiObjects.getActivity().getReviewGroupList())) {
			groups.addAll(aeiObjects.getActivity().getReviewGroupList());
		}
		identities.addAll(aeiObjects.business().organization().identity().listWithGroup(groups));
		identities.addAll(aeiObjects.business().organization().identity().listWithUnitSubNested(units));
		identities = ListTools.trim(identities, true, true);
		logger.debug("work title:{}, id:{}, activity name:{}, id:{}, translate review identity: {}",
				aeiObjects.getWork().getTitle(), aeiObjects.getWork().getId(), aeiObjects.getActivity().getName(),
				aeiObjects.getActivity().getId(), XGsonBuilder.toJson(identities));
		List<String> os = aeiObjects.business().organization().person().listWithIdentity(identities);
		if (os.size() != identities.size()) {
			logger.warn(
					"work title:{}, id:{}, activity name:{}, id:{}, translate review identity: {}, result not with same length: {}.",
					aeiObjects.getWork().getTitle(), aeiObjects.getWork().getId(), aeiObjects.getActivity().getName(),
					aeiObjects.getActivity().getId(), XGsonBuilder.toJson(identities), XGsonBuilder.toJson(os));
		}
		return os;
	}

	/* 取到指定职务的identity */
	private static List<String> duty(AeiObjects aeiObjects) throws Exception {
		List<String> list = new ArrayList<>();
		if (StringUtils.isNotEmpty(aeiObjects.getActivity().getReviewDuty())) {
			JsonArray array = XGsonBuilder.instance().fromJson(aeiObjects.getActivity().getReviewDuty(),
					JsonArray.class);
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
	private static List<String> script(AeiObjects aeiObjects) throws Exception {
		List<String> list = new ArrayList<>();
		if ((StringUtils.isNotEmpty(aeiObjects.getActivity().getReviewScript()))
				|| (StringUtils.isNotEmpty(aeiObjects.getActivity().getReviewScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects);
			List<String> os = scriptHelper.evalExtrectDistinguishedName(aeiObjects.getWork().getApplication(),
					aeiObjects.getActivity().getReviewScript(), aeiObjects.getActivity().getReviewScriptText());
			if (ListTools.isNotEmpty(os)) {
				list.addAll(os);
			}
		}
		return list;
	}

	private static List<String> data(Business business, Data data, Activity activity) throws Exception {
		List<String> list = new ArrayList<>();
		if (ListTools.isNotEmpty(activity.getReviewDataPathList())) {
			for (String str : ListTools.trim(activity.getReviewDataPathList(), true, true)) {
				if (StringUtils.isNotEmpty(str)) {
					list.addAll(data.extractDistinguishedName(str));
				}
			}
		}
		return list;
	}

}
